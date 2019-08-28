package com.mirandox.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.mirandox.cursomc.domain.Categoria;
import com.mirandox.cursomc.repositories.CategoriaRepository;
import com.mirandox.cursomc.services.exceptions.DataIntegrityException;
import com.mirandox.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class CategoriaService {
	
	private static String NOT_DELETE_CATEGORIA = "Não é possível excluír uma categoria que possui produtos associados!";

	@Autowired
	private CategoriaRepository categoriaRepository;
	
	public Categoria find(Integer id) {
		Optional<Categoria> categoria = categoriaRepository.findById(id);
		
		return categoria.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Categoria.class.getName()));
	}
	
	public Categoria insert(Categoria categoria) {
		categoria.setId(null);
		return categoriaRepository.save(categoria);
	}
	
	public Categoria update(Categoria categoria) {
		find(categoria.getId());
		return categoriaRepository.save(categoria);
	}
	
	public void delete(Integer id) {
		find(id);
		try {
			categoriaRepository.deleteById(id);
		} catch(DataIntegrityViolationException e) {
			throw new DataIntegrityException(NOT_DELETE_CATEGORIA);
		}
 	}
	
	public List<Categoria> findAll() {
		return categoriaRepository.findAll();
	}
	
	public Page<Categoria> findPage(Integer page, Integer linesPerPage, String direction, String orderBy) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy); 
		return categoriaRepository.findAll(pageRequest);
	}
}
