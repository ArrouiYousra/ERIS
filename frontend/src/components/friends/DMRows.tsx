import { useNavigate } from "react-router-dom";

export function DMRows() {
  const navigate = useNavigate();

  return (
    <div className="dm-bar relative h-full w-14 flex items-center flex-col">
      <button
        type="button"
        className="flex items-center justify-center w-12 h-12 max-sm:w-10 max-sm:h-10 rounded-full bg-[#1a1d24] text-[#3ba55d] hover:rounded-xl hover:bg-[#3ba55d] hover:text-white transition-all duration-200 shrink-0 mt-1"
        title="Envoyer un message"
        aria-label="Envoyer un message"
        onClick={() => navigate("/app")}
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="24"
          height="24"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2.5"
          stroke-linecap="round"
          stroke-linejoin="round"
          className="lucide lucide-plus w-6 h-6"
        >
          <path d="M5 12h14"></path>
          <path d="M12 5v14"></path>
        </svg>
      </button>
    </div>
  );
}
