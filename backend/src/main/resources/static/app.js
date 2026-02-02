'use strict';

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const chatArea = document.querySelector('#chat-messages');
const logout = document.querySelector('#logout');

let stompClient = null;
let nickname = null;
let fullname = null;
let selectedUserId = null;

// ✅ NOUVELLES VARIABLES pour le typing
let typingTimer;
let isTyping = false;
const TYPING_TIMEOUT = 5000; // 2 secondes

function connect(event) {
    event.preventDefault();

    nickname = document.querySelector('#nickname').value.trim();
    fullname = document.querySelector('#fullname').value.trim();

    if (nickname && fullname) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
}

function onConnected() {
    console.log('✅ WebSocket connected!');

    // S'abonner aux messages privés
    stompClient.subscribe(`/user/${nickname}/queue/messages`, onMessageReceived);

    // S'abonner au topic public pour les connexions/déconnexions
    stompClient.subscribe('/topic/public', onUserStatusUpdate);

    // ✅ NOUVEAU : S'abonner aux notifications typing
    stompClient.subscribe(`/user/${nickname}/queue/typing`, onTypingReceived);

    // Enregistrer l'utilisateur
    stompClient.send("/app/user.addUser", {}, JSON.stringify({
        displayName: nickname,
        fullName: fullname,
        status: 'ONLINE'
    }));

    document.querySelector('#connected-user-fullname').textContent = fullname;

    setTimeout(() => {
        findAndDisplayConnectedUsers();
    }, 300);
}

// Gérer les notifications de connexion/déconnexion
function onUserStatusUpdate(payload) {
    console.log('👤 User status update:', payload.body);
    findAndDisplayConnectedUsers();
}

async function findAndDisplayConnectedUsers() {
    try {
        console.log('📡 Fetching connected users...');
        const response = await fetch('/users');

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        let users = await response.json();
        console.log('👥 All users:', users);

        users = users.filter(user => user.displayName !== nickname);
        console.log('👥 Other users:', users);

        const usersList = document.getElementById('connectedUsers');
        usersList.innerHTML = '';

        if (users.length === 0) {
            const emptyMsg = document.createElement('li');
            emptyMsg.textContent = 'No other users online';
            emptyMsg.style.color = '#fff';
            emptyMsg.style.fontStyle = 'italic';
            emptyMsg.style.padding = '10px';
            usersList.appendChild(emptyMsg);
            return;
        }

        users.forEach((user, index) => {
            appendUserElement(user, usersList);
            if (index < users.length - 1) {
                const separator = document.createElement('li');
                separator.classList.add('separator');
                usersList.appendChild(separator);
            }
        });
    } catch (error) {
        console.error('❌ Error fetching users:', error);
    }
}

function appendUserElement(user, usersList) {
    const li = document.createElement('li');
    li.classList.add('user-item');
    li.id = user.displayName;

    const img = document.createElement('img');
    img.src = '../img/user_icon.png';
    img.alt = user.fullName;
    img.onerror = function() {
        this.style.display = 'none';
    };

    const nameSpan = document.createElement('span');
    nameSpan.textContent = user.fullName || user.displayName;

    const badge = document.createElement('span');
    badge.textContent = '0';
    badge.classList.add('nbr-msg', 'hidden');

    li.appendChild(img);
    li.appendChild(nameSpan);
    li.appendChild(badge);

    li.addEventListener('click', userItemClick);

    usersList.appendChild(li);
}

function userItemClick(event) {
    document.querySelectorAll('.user-item').forEach(item => {
        item.classList.remove('active');
    });

    const clickedUser = event.currentTarget;
    clickedUser.classList.add('active');

    messageForm.classList.remove('hidden');

    selectedUserId = clickedUser.getAttribute('id');

    const badge = clickedUser.querySelector('.nbr-msg');
    if (badge) {
        badge.classList.add('hidden');
        badge.textContent = '0';
    }

    fetchAndDisplayUserChat();
}

function displayMessage(senderId, content) {
    const messageDiv = document.createElement('div');
    messageDiv.classList.add('message');

    if (senderId === nickname) {
        messageDiv.classList.add('sender');
    } else {
        messageDiv.classList.add('receiver');
    }

    const p = document.createElement('p');
    p.textContent = content;
    messageDiv.appendChild(p);
    chatArea.appendChild(messageDiv);
}

async function fetchAndDisplayUserChat() {
    try {
        console.log(`📨 Loading chat with ${selectedUserId}...`);
        const response = await fetch(`/messages/${nickname}/${selectedUserId}`);
        const messages = await response.json();

        chatArea.innerHTML = '';
        messages.forEach(msg => {
            displayMessage(msg.senderId, msg.content);
        });
        chatArea.scrollTop = chatArea.scrollHeight;
    } catch (error) {
        console.error('❌ Error loading chat:', error);
    }
}

function sendMessage(event) {
    event.preventDefault();

    const content = messageInput.value.trim();
    if (!content || !stompClient || !selectedUserId) {
        return;
    }

    const chatMessage = {
        senderId: nickname,
        recipientId: selectedUserId,
        content: content,
        timestamp: new Date()
    };

    console.log('📤 Sending message:', chatMessage);

    stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
    displayMessage(nickname, content);
    messageInput.value = '';
    chatArea.scrollTop = chatArea.scrollHeight;

    // ✅ NOUVEAU : Arrêter le statut typing après envoi
    if (isTyping) {
        isTyping = false;
        sendTypingStatus(false);
    }
}

function onMessageReceived(payload) {
    console.log('📩 Message received:', payload.body);

    const message = JSON.parse(payload.body);

    if (selectedUserId && selectedUserId === message.senderId) {
        console.log('💬 Displaying message in active chat');
        displayMessage(message.senderId, message.content);
        chatArea.scrollTop = chatArea.scrollHeight;
    } else {
        console.log('🔔 Showing notification badge');
        const userItem = document.querySelector(`#${message.senderId}`);
        if (userItem) {
            const badge = userItem.querySelector('.nbr-msg');
            if (badge) {
                badge.classList.remove('hidden');
                let count = parseInt(badge.textContent) || 0;
                badge.textContent = count + 1;
            }
        }
    }
}

// ✅ NOUVELLE FONCTION : Gérer les notifications typing
function onTypingReceived(payload) {
    console.log('⌨️ Typing notification received:', payload.body);

    const notification = JSON.parse(payload.body);

    // Afficher uniquement si c'est la conversation active
    if (selectedUserId === notification.senderId) {
        const typingIndicator = document.getElementById('typing-indicator');

        if (notification.typing === 'TYPING') {
            typingIndicator.textContent = `${notification.senderId} est en train d'écrire...`;
            typingIndicator.classList.remove('hidden');
            console.log('✅ Showing typing indicator');
        } else {
            typingIndicator.classList.add('hidden');
            console.log('❌ Hiding typing indicator');
        }
    }
}

// ✅ NOUVELLE FONCTION : Envoyer le statut typing
function sendTypingStatus(isTypingNow) {
    if (!stompClient || !selectedUserId) return;

    const typingNotification = {
        senderId: nickname,
        recipientId: selectedUserId,
        typing: isTypingNow ? 'TYPING' : 'NOT_TYPING'
    };

    console.log('📤 Sending typing status:', typingNotification);
    stompClient.send('/app/user.typing', {}, JSON.stringify(typingNotification));
}

// ✅ NOUVEAU : Détecter quand l'utilisateur tape
messageInput.addEventListener('input', function(event) {
    const content = event.target.value.trim();

    if (content.length > 0 && selectedUserId) {
        // Si pas encore en train de taper, envoyer "TYPING"
        if (!isTyping) {
            isTyping = true;
            sendTypingStatus(true);
            console.log('🔤 Started typing');
        }

        // Réinitialiser le timer
        clearTimeout(typingTimer);

        // Si l'utilisateur arrête de taper pendant 2 secondes
        typingTimer = setTimeout(() => {
            isTyping = false;
            sendTypingStatus(false);
            console.log('⏸️ Stopped typing');
        }, TYPING_TIMEOUT);
    } else {
        // Champ vide = arrêter le statut "typing"
        clearTimeout(typingTimer);
        if (isTyping) {
            isTyping = false;
            sendTypingStatus(false);
            console.log('⏸️ Input cleared');
        }
    }
});

function onError(error) {
    console.error('❌ WebSocket error:', error);
    alert('Could not connect to WebSocket. Please refresh and try again.');
}

function onLogout() {
    if (stompClient && stompClient.connected) {
        stompClient.send("/app/user.disconnectUser", {}, JSON.stringify({
            displayName: nickname,
            fullName: fullname,
            status: 'OFFLINE'
        }));
    }
    window.location.reload();
}

usernameForm.addEventListener('submit', connect);
messageForm.addEventListener('submit', sendMessage);
logout.addEventListener('click', onLogout);
window.onbeforeunload = () => onLogout();