package com.mirandox.cursomc.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mirandox.cursomc.domain.Cidade;
import com.mirandox.cursomc.domain.Estado;
import com.mirandox.cursomc.dto.CidadeDTO;
import com.mirandox.cursomc.dto.EstadoDTO;
import com.mirandox.cursomc.services.CidadeService;
import com.mirandox.cursomc.services.EstadoService;

@RestController
@RequestMapping(value="/estados")
public class EstadoResource {

	@Autowired
	private EstadoService estadoService;
	
	@Autowired
	private CidadeService cidadeService;
	
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<List<EstadoDTO>> findAll() {
		List<Estado> listaEstado = estadoService.findAll();
		List<EstadoDTO> listaEstadoDto = listaEstado.stream().map(obj -> new EstadoDTO(obj)).collect(Collectors.toList());
		return ResponseEntity.ok().body(listaEstadoDto);
	}
	
	@RequestMapping(value="{estadoId}/cidades", method=RequestMethod.GET)
	public ResponseEntity<List<CidadeDTO>> findCidades(@PathVariable Integer estadoId) {
		List<Cidade> listaCidade = cidadeService.findByEstado(estadoId);
		List<CidadeDTO> listaCidadeDto = listaCidade.stream().map(obj -> new CidadeDTO(obj)).collect(Collectors.toList());
		return ResponseEntity.ok().body(listaCidadeDto);
	}
}
