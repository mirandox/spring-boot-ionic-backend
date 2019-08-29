package com.mirandox.cursomc.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.mirandox.cursomc.domain.Cliente;
import com.mirandox.cursomc.domain.enums.TipoCliente;
import com.mirandox.cursomc.dto.ClienteNewDTO;
import com.mirandox.cursomc.repositories.ClienteRepository;
import com.mirandox.cursomc.resources.exceptions.FieldMessage;
import com.mirandox.cursomc.services.validation.utils.BR;

public class ClienteInsertValidator implements ConstraintValidator<ClienteInsert, ClienteNewDTO> {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Override
	public void initialize(ClienteInsert ann) {
	}

	@Override
	public boolean isValid(ClienteNewDTO clienteNewDTO, ConstraintValidatorContext context) {
		
		List<FieldMessage> list = new ArrayList<>();

		if(clienteNewDTO.getTipo().equals(TipoCliente.PESSOAFISICA.getCod()) && !BR.isValidCPF(clienteNewDTO.getCpfOuCnpj()))
			list.add(new FieldMessage("cpfOuCnpj", "CPF inválido!"));
		
		if(clienteNewDTO.getTipo().equals(TipoCliente.PESSOAJURIDICA.getCod()) && !BR.isValidCNPJ(clienteNewDTO.getCpfOuCnpj()))
			list.add(new FieldMessage("cpfOuCnpj", "CNPJ inválido!"));
		
		Cliente cliente = clienteRepository.findByEmail(clienteNewDTO.getEmail());
		
		if(cliente != null)
			list.add(new FieldMessage("email", "Email já existente!"));
		
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}
