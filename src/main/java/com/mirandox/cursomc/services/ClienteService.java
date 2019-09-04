package com.mirandox.cursomc.services;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mirandox.cursomc.domain.Cidade;
import com.mirandox.cursomc.domain.Cliente;
import com.mirandox.cursomc.domain.Endereco;
import com.mirandox.cursomc.domain.enums.Perfil;
import com.mirandox.cursomc.domain.enums.TipoCliente;
import com.mirandox.cursomc.dto.ClienteDTO;
import com.mirandox.cursomc.dto.ClienteNewDTO;
import com.mirandox.cursomc.repositories.ClienteRepository;
import com.mirandox.cursomc.repositories.EnderecoRepository;
import com.mirandox.cursomc.security.UserSS;
import com.mirandox.cursomc.services.exceptions.AuthorizationException;
import com.mirandox.cursomc.services.exceptions.DataIntegrityException;
import com.mirandox.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {
	
	private static String NOT_DELETE_CLIENTE = "Não é possível excluír porque há pedidos relacionados!";

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private S3Service s3Service;
	
	public Cliente find(Integer id) {
		UserSS user = UserService.authenticated();
		if(user == null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId()))
			throw new AuthorizationException("Acesso negado!");
		
		Optional<Cliente> cliente = clienteRepository.findById(id);
		return cliente.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}
	
	@Transactional
	public Cliente insert(Cliente cliente) {
		cliente.setId(null);
		cliente = clienteRepository.save(cliente);
		enderecoRepository.saveAll(cliente.getEnderecos());
		return cliente;
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
		return new Cliente(clienteDTO.getId(), clienteDTO.getNome(), clienteDTO.getEmail(), null, null, null);
	}
	
	public Cliente fromDTO(ClienteNewDTO clienteNewDTO) {
		Cliente cliente = new Cliente(null, clienteNewDTO.getNome(), clienteNewDTO.getEmail(), 
				clienteNewDTO.getCpfOuCnpj(), TipoCliente.toEnum(clienteNewDTO.getTipo()), 
				bCryptPasswordEncoder.encode(clienteNewDTO.getSenha()));
		
		Cidade cidade = new Cidade(clienteNewDTO.getCidadeId(), null, null);
		
		Endereco endereco = new Endereco(null, clienteNewDTO.getLogradouro(), clienteNewDTO.getNumero(), 
				clienteNewDTO.getComplemento(), clienteNewDTO.getBairro(), clienteNewDTO.getCep(), cliente, cidade);
		
		cliente.getEnderecos().add(endereco);
		cliente.getTelefones().add(clienteNewDTO.getTelefone1());
		
		if(clienteNewDTO.getTelefone2() != null) {
			cliente.getTelefones().add(clienteNewDTO.getTelefone2());
		}
		
		if(clienteNewDTO.getTelefone3() != null) {
			cliente.getTelefones().add(clienteNewDTO.getTelefone3());
		}
		
		return cliente;
	}
	
	private void updateData(Cliente clienteFind, Cliente cliente) {
		clienteFind.setNome(cliente.getNome());
		clienteFind.setEmail(cliente.getEmail());
	}
	
	public URI uploadProfilePicture(MultipartFile multipartFile) {
		return s3Service.uploadFile(multipartFile);
	}
}
