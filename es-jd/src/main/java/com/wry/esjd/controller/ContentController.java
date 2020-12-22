package com.wry.esjd.controller;

import com.wry.esjd.service.ContentJDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 请求 服务处
 * </p>
 *
 * @author wangruiyu
 * @since 2020/8/3
 */
@RestController
@RequestMapping("/content")
public class ContentController {

    @Autowired
    private ContentJDService contentJDService;

    @GetMapping("/parse/{keyword}")
    public Boolean parse(@PathVariable String keyword) throws IOException {
        return contentJDService.parseContentJD(keyword);
    }

    @GetMapping("/search/{keyword}/{pageNum}/{pageSize}")
    public List<Map<String, Object>> searchPage(@PathVariable String keyword,
                                                @PathVariable int pageNum,
                                                @PathVariable int pageSize) throws IOException {
        return contentJDService.searchPage(keyword, pageNum, pageSize);
    }
}
