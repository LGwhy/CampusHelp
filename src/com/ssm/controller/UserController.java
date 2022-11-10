package com.ssm.controller;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.ssm.po.User;
import com.ssm.service.AdminService;
import com.ssm.service.UserService;

/**
 * ***********用户基本************** 
 * 用户登录 *
 * 用户注册*
 * 
 * 用户信息更新* 
 * 用户密码更新*
 * 
 * @author
 *
 */
@Controller
@SessionAttributes({ "nowuser" })
@RequestMapping(value = "user/")
public class UserController {

	@Resource(name = "userService")
	public UserService userService;

	@Resource(name = "adminService")
	public AdminService adminService;

	// 登录
	@RequestMapping("login.do")
	public String login(String studentid, String password, Model model) {
		User user = null;
		user = userService.login(studentid);

		if (user == null) {
			model.addAttribute("msg", "登录失败--账号不存在");//往前台传入错误提示信息
			return "login";
		}

		if (password.equals(user.getPassword())) {
			model.addAttribute("nowuser", user);
			model.addAttribute("loginflag", "loginflag");//存储参数供用户首页使用
			return "index";
		} else {
			model.addAttribute("msg", "登录失败--请确定账号和密码正确");
			return "login";
		}
	}

	// 注册
	@RequestMapping("register.do")
	public String register(User user, Model model) {
		// 检查学号账号重复
		// getAccountCount()查询账号个数，用户输入的学号对于账号个数大于0，则说明账号已存在
		int countnum = userService.getAccountCount(user.getStudentid());
		if (countnum > 0) {
			model.addAttribute("msg", "该学号已经注册");
			model.addAttribute("user", user);
			return "register";
		}
		// 设置参数
		user.setStuid(0);
		user.setRegistertime(new Date());
		user.setMoney(0.00);
		user.setState(0);
		System.out.println(user.toString());
		// 验证注册信息是否符合条件
		int result = userService.setUser(user);
		if (result <= 0) {
			model.addAttribute("msg", "注册失败");
			model.addAttribute("user", user);
			return "register";
		}
		model.addAttribute("msg", "注册成功，请登录");
		return "login";
	}

	// 更新用户信息
	@RequestMapping("update.do")
	public String update(HttpServletRequest request, User user, Model model) {
		int r = userService.updateUserInfo(user);
		//验证信息是否符合格式
		if (r > 0) {
			model.addAttribute("msg", "更新成功，请重新登录");
			model.addAttribute("flag", "更新成功，请重新登录");
			return "userUpdate";
		}
		model.addAttribute("msg", "更新失败");
		return "userUpdate";
	}

	// 修改密码
	@RequestMapping("updatepwd.do")
	public String updatepwd(HttpServletRequest request, String oldpassword, User user, Model model) {
		//获取旧密码
		User olduser = (User) request.getSession(false).getAttribute("nowuser");
		System.out.println(olduser.getPassword());
		System.out.println(oldpassword);
		if (!olduser.getPassword().equals(oldpassword)) {
			model.addAttribute("msg", "原密码错误");
			return "userPassword";
		}
		int r = userService.updateUserInfo(user);
		if (r > 0) {
			model.addAttribute("msg", "修改成功，请重新登录");
			model.addAttribute("flag", "更新成功，请重新登录");
			return "userPassword";
		}
		model.addAttribute("msg", "修改失败");
		return "userPassword";
	}
}
