package com.cell.spzx.role_manage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cell.model.entity.system.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleManageMapper extends BaseMapper<SysRole>{

    List<SysRole> selectAll(@Param("roleName") String roleName, @Param("roleCode") String roleCode);

    void deleteSysRoleByMB(@Param("ids") List<Long> ids);

    void updateSysRoleByMB(SysRole sysRole);

}
