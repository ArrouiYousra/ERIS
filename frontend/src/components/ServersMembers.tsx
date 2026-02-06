import { useServerMember } from "../hooks/useServers";
import type { ServerMember } from "../api/serverMembersApi";

interface ServerMembersProps {
  serverId: number;
}

export function ServerMembers({ serverId }: ServerMembersProps) {
  const { data: members = [], isLoading } = useServerMember(serverId);

  if (isLoading) return <div>Loading members...</div>;
  if (!members.length) return <div>No members found</div>;

  return (
    <ul>
      {members.map((member: ServerMember) => (
        <li key={member.id}>{member.nickname}</li>
      ))}
    </ul>
  );
}
