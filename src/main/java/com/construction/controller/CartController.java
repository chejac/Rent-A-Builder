package com.construction.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.construction.entity.Order;
import com.construction.entity.ProductDetail;
import com.construction.entity.User;
import com.construction.service.ProductDetailService;
import com.construction.service.CartService;
import com.construction.service.OrderService;
import com.construction.service.UserService;
import com.construction.validator.OrderValidator;

/**
 * Controller for the cart pages
 * @author Admin
 */
@Controller
@RequestMapping("/cart")
public class CartController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private ProductDetailService productDetailService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderValidator orderValidator;
	
	/**
	 * Bind the formatted date to the entity
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
    }

	/**
	 * View all the products in the cart of the user
	 * @param model
	 * @param principal
	 * @return jsp
	 */
	@GetMapping("/")
	public String cartManager(Model model, Principal principal) {
		User user = userService.getByUsername(principal.getName());
		Long cartId = user.getCart().getId();
		Set<ProductDetail> productDetails = cartService.getById(cartId).getProductDetails();
		model.addAttribute("allCartDetails", productDetails);
		
		return "cart/cart_manager";
	}
	
	/**
	 * Update the product in the cart
	 * @param inputValue
	 * @param cartDetailId
	 * @return redirect
	 */
	@GetMapping("/update")
	public String updateCart(@RequestParam int inputValue, @RequestParam Long cartDetailId) {
		productDetailService.update(cartDetailId, inputValue);
		
		return "redirect:/cart/";
	}
	
	/**
	 * Delete the product in the cart
	 * @param id
	 * @return redirect
	 */
	@GetMapping("/delete")
	public String deleteCart(@RequestParam Long id) {
		productDetailService.deleteById(id);
		
		return "redirect:/cart/";
	}
	
	/**
	 * Submit the cart as an order
	 * @param model
	 * @return jsp
	 */
	@GetMapping("/order")
	public String submitOrder(Model model) {
		model.addAttribute("orderForm", new Order());
		
		return "cart/submit_order";
	}
	
	/**
	 * Add the order to the database
	 * @param principal
	 * @param orderForm
	 * @param bindingResult
	 * @return redirect
	 */
	@PostMapping("/order")
	public String processOrder(Principal principal, @ModelAttribute("orderForm") Order orderForm,
			BindingResult bindingResult) {
		orderValidator.validate(orderForm, bindingResult);
		
		if (bindingResult.hasErrors()) return "cart/submit_order";
		
		User user = userService.getByUsername(principal.getName());
		orderService.addToOrder(orderForm, user);
		
		return "redirect:/order/?id=" + orderForm.getId();
	}
	
}
