package com.example.fulbrincjava.bootstrap;


import com.example.fulbrincjava.entities.Role;
import com.example.fulbrincjava.entities.RoleEnum;
import com.example.fulbrincjava.repositories.RoleRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RoleSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;


    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent contextRefreshedEvent) {
        this.loadRoles();
    }

    private void loadRoles() {
        RoleEnum[] roleNames = new RoleEnum[] { RoleEnum.USER, RoleEnum.MODERATOR, RoleEnum.ADMIN };
        Map<RoleEnum, String> roleDescriptionMap = Map.of(
                RoleEnum.USER, "Default user role",
                RoleEnum.MODERATOR, "Moderator role",
                RoleEnum.ADMIN, "Administrator role"
        );

        for (RoleEnum roleName : roleNames) {
            Optional<Role> optionalRole = roleRepository.findByName(roleName);

            optionalRole.ifPresentOrElse(System.out::println, () -> {
                Role roleToCreate = new Role();

                roleToCreate.setName(roleName)
                        .setDescription(roleDescriptionMap.get(roleName));

                roleRepository.save(roleToCreate);
            });
        }
    }
}
