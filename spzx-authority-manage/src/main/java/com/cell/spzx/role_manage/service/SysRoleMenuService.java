package com.cell.spzx.role_manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cell.model.dto.system.SysRoleMenuDto;
import com.cell.model.entity.system.SysMenu;
import com.cell.model.entity.system.SysRoleMenu;
import com.cell.model.vo.system.SysMenuVo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface SysRoleMenuService extends IService<SysRoleMenu> {

    void menuAllocation(SysRoleMenuDto sysRoleMenuDto);

    List<Long> getRoleHasMenu(Long id);

    List<SysMenuVo> getUsableMenu(Long roleId);

    List<SysMenu> getUsableMenuWithSySMenu(Long roleId);

    List<SysMenuVo> getDynamicMenu(HttpServletRequest request);

}
