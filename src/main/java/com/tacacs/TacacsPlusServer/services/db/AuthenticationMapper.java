package com.tacacs.TacacsPlusServer.services.db;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.tacacs.TacacsPlusServer.services.db.entity.authenEntity;
@Repository
public interface AuthenticationMapper {
 public List<authenEntity> getUserInfo(Map<String, Object> parm);

}
