package com.hdconsulting.springboot.backend.chat.controllers;

import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.hdconsulting.springboot.backend.chat.models.documents.Mensaje;
import com.hdconsulting.springboot.backend.chat.models.service.ChatService;

@Controller
public class ChatController {
	
	private final String[] colores = {"red", "green", "blue", "magenta", "purple", "orange"}; 
	
	@Autowired
	private ChatService chatservice;
	
	@Autowired
	private SimpMessagingTemplate webSocket;
	
	@MessageMapping("/mensaje") 
	@SendTo("/chat/mensaje") 
	public Mensaje recibeMensaje(Mensaje mensaje) {
		mensaje.setFecha(new Date().getTime());
		
		if ("NUEVO_USUARIO".equals(mensaje.getTipo())){
			mensaje.setTexto("nuevo usuario ");
			mensaje.setColor(colores[new Random().nextInt(colores.length)]);
		}
		else {
			chatservice.guardar(mensaje);
		}
		
		return mensaje;
	}

	@MessageMapping("/escribiendo") //destino par avisar al servidor,  cuando alquier escribe en el cat 
	@SendTo("/chat/escribiendo") // es par avisar al resto de los usuarios (todos los que estan connectado al chat
	public String estaEscirbiendo(String username) {
		return username.concat(" esta escribiendo…");
		
	}
	
	@MessageMapping("/historial")
	//@SendTo("/chat/historial")
	public void historial(String clienteId){
		/* Pour remplacer le sendTo, vu qu'il sera parametrable*/
		webSocket.convertAndSend("/chat/historial/" +  clienteId, chatservice.obtenerUltimos10Mensajes());
	}
}
