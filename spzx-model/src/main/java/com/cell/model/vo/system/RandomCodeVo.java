package com.cell.model.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录界面生成随机验证码实体类")
public class RandomCodeVo {
    private String codeKey ; // 验证码的 key
    private String codeValue ; // 图片验证码对应的字符串数据
}
