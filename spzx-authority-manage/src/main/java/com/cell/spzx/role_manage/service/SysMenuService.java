package com.cell.spzx.role_manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cell.model.dto.h5.SysMenuDto;
import com.cell.model.entity.system.SysMenu;
import com.cell.model.vo.system.SysMenuVo;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> getAllMenu();

    void addSysMenu(SysMenu sysMenu);

    SysMenu getMenuById(Long id);

    void updateSysMenu(SysMenuDto sysMenuDto);

    void deleteSysMenu(Long id);

}
