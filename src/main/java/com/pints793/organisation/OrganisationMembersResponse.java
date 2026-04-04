package com.pints793.organisation;

import com.pints793.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter @Accessors(chain = true)
public class OrganisationMembersResponse {
    Map<String, String> members = new HashMap<>();
    Map<String, String> admins = new HashMap<>();

    public void addMember(User user){
        if (user == null) {
            return;
        }
        members.put(user.getId(), user.getUsername());
    }

    public void addAdmin(User user){
        if (user == null) {
            return;
        }
        admins.put(user.getId(), user.getUsername());
    }
}
