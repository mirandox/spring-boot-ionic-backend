package com.mirandox.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.mirandox.cursomc.domain.Pedido;

public interface EmailService {

	void sendOrderConfirmationEmail(Pedido pedido);
	
	void sendEmail(SimpleMailMessage msg);
}
