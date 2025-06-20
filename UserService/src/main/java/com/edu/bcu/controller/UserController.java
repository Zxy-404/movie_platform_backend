package com.edu.bcu.controller;

import com.edu.bcu.common.Result;
import com.edu.bcu.dto.UserLoginDTO;
import com.edu.bcu.dto.UserRegisterDTO;
import com.edu.bcu.entity.User;
import com.edu.bcu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "用户注册", description = "新用户注册接口")
    @ApiResponse(responseCode = "200", description = "注册成功", 
            content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Result.class)))
    @PostMapping("/register")
    public Result<User> register(@RequestBody @Valid UserRegisterDTO registerDTO) {
        User user = userService.register(registerDTO);
        return Result.success("注册成功", user);
    }

    @Operation(summary = "用户登录", description = "用户登录认证接口")
    @ApiResponse(responseCode = "200", description = "登录成功")
    @PostMapping("/login")
    public Result<User> login(@RequestBody @Valid UserLoginDTO loginDTO) {
        User user = userService.login(loginDTO);
        return Result.success("登录成功", user);
    }

    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户详细信息")
    @Parameter(name = "userId", description = "用户ID", required = true)
    @GetMapping("/{userId}")
    public Result<User> getUserInfo(@PathVariable Integer userId) {
        User user = userService.getUserById(userId);
        return Result.success(user);
    }

    @Operation(summary = "更新用户信息", description = "更新指定用户的基本信息")
    @Parameters({
        @Parameter(name = "userId", description = "用户ID", required = true),
        @Parameter(name = "user", description = "用户信息", required = true)
    })
    @PutMapping("/{userId}")
    public Result<User> updateUser(@PathVariable Integer userId, @RequestBody User user) {
        User updatedUser = userService.updateUser(userId, user);
        return Result.success("更新成功", updatedUser);
    }

    @Operation(summary = "修改密码", description = "修改指定用户的密码")
    @Parameters({
        @Parameter(name = "userId", description = "用户ID", required = true),
        @Parameter(name = "oldPassword", description = "原密码", required = true),
        @Parameter(name = "newPassword", description = "新密码", required = true)
    })
    @PutMapping("/{userId}/password")
    public Result<Void> updatePassword(
            @PathVariable Integer userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword
    ) {
        userService.updatePassword(userId, oldPassword, newPassword);
        return Result.success("密码修改成功", null);
    }

    @Operation(summary = "删除用户", description = "删除指定用户账号")
    @Parameter(name = "userId", description = "用户ID", required = true)
    @DeleteMapping("/{userId}")
    public Result<Void> deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
        return Result.success("账户注销成功", null);
    }

    @Operation(summary = "检查用户名可用性", description = "检查用户名是否可用")
    @Parameter(name = "username", description = "用户名", required = true)
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        boolean available = userService.checkUsernameAvailable(username);
        return Result.success(available);
    }

    @Operation(summary = "分页获取用户列表", description = "分页获取所有用户信息")
    @Parameters({
        @Parameter(name = "page", description = "页码(从0开始)", required = false),
        @Parameter(name = "size", description = "每页大小", required = false),
        @Parameter(name = "sort", description = "排序字段", required = false)
    })
    @GetMapping("/list")
    public Result<Page<User>> getUserList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime") String sort,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<User> userPage = userService.getAllUsers(pageable);
        return Result.success("获取成功", userPage);
    }

    @Operation(summary = "搜索用户", description = "根据关键词搜索用户(支持用户名、邮箱、手机号)")
    @Parameters({
        @Parameter(name = "keyword", description = "搜索关键词", required = true),
        @Parameter(name = "page", description = "页码(从0开始)", required = false),
        @Parameter(name = "size", description = "每页大小", required = false),
        @Parameter(name = "sort", description = "排序字段", required = false)
    })
    @GetMapping("/search")
    public Result<Page<User>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime") String sort,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<User> userPage = userService.searchUsers(keyword, pageable);
        return Result.success("搜索成功", userPage);
    }
}