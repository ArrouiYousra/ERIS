package Fourth_Argument.eris.api.mapper;

import org.springframework.stereotype.Component;

import Fourth_Argument.eris.api.dto.response.RoleResponseDTO;
import Fourth_Argument.eris.api.model.Role;

@Component
public class RoleMapper {
    public RoleResponseDTO toDTO(Role role) {
        return new RoleResponseDTO(role.getId(), role.getName());
    }
}
