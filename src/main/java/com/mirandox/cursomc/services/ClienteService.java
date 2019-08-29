package com.mirandox.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.mirandox.cursomc.domain.Cliente;
import com.mirandox.cursomc.dto.ClienteDTO;
import com.mirandox.cursomc.repositories.ClienteRepository;
import com.mirandox.cursomc.services.exceptions.DataIntegrityException;
import com.mirandox.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {
	
	private static String NOT_DELETE_CLIENTE = "Não é possível excluír porque há entidades relacionadas!";

	@Autowired
	private ClienteRepository clienteRepository;
	
	public Cliente find(Integer id) {
		Optional<Cliente> cliente = clienteRepository.findById(id);
		
		return cliente.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}
	
	public Cliente update(Cliente cliente) {
		Cliente clienteFind = find(cliente.getId());
		updateData(clienteFind, cliente);
		return clienteRepository.save(clienteFind);
	}
	
	public void delete(Integer id) {
		find(id);
		try {
			clienteRepository.deleteById(id);
		} catch(DataIntegrityViolationException e) {
			throw new DataIntegrityException(NOT_DELETE_CLIENTE);
		}
 	}
	
	public List<Cliente> findAll() {
		return clienteRepository.findAll();
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String direction, String orderBy) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy); 
		return clienteRepository.findAll(pageRequest);
	}
	
	public Cliente fromDTO(ClienteDTO clienteDTO) {
		return new Cliente(clienteDTO.getId(), clienteDTO.getNome(), clienteDTO.getEmail(), null, null);
	}
	
	private void updateData(Cliente clienteFind, Cliente cliente) {
		clienteFind.setNome(cliente.getNome());
		clienteFind.setEmail(cliente.getEmail());
	}
}
