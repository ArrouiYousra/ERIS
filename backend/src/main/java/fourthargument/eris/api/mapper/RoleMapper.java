package fourthargument.eris.api.mapper;

import org.springframework.stereotype.Component;

import fourthargument.eris.api.dto.response.RoleResponseDTO;
import fourthargument.eris.api.model.Role;

@Component
public class RoleMapper {
    public RoleResponseDTO toDTO(Role role) {
        return new RoleResponseDTO(role.getId(), role.getName());
    }
}
