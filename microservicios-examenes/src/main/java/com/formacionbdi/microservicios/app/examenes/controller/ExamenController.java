package com.formacionbdi.microservicios.app.examenes.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.formacionbdi.microservicios.app.examenes.services.ExamenService;
import com.formacionbdi.microservicios.commons.controllers.CommonController;
import com.formacionbdi.microservicios.commons.examenes.models.entity.Examen;
import com.formacionbdi.microservicios.commons.examenes.models.entity.Pregunta;

@RestController
public class ExamenController extends CommonController <Examen, ExamenService>{
	
	@PutMapping("/{id}")
	public ResponseEntity<?>editar (@Valid @RequestBody Examen examen, BindingResult result, @PathVariable Long id){
		
		if(result.hasErrors()) {
			return validar(result);
		}
		
		//Recibimos el optional del examen y usamos el service para buscar por id
		Optional<Examen> o = service.findById(id);
		
		//valoramos si no esta presente, si no lo esta devolvemos un notfound
		if(!o.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		//Aqui ya sabemos que esta, asi que obtenemos el examen
		Examen examenDb = o.get();
		//modificamos el nombre para darle el del examen recibido
		examenDb.setNombre(examen.getNombre());
		
		//creamos una lista donde guardaremos las preguntas eliminadas
		List<Pregunta> eliminadas = new ArrayList<>();
		
		//iteramos cada pregunta
		examenDb.getPreguntas().forEach(pdb ->{
			//preguntamos si cada pregunta no existe en la bd
			if(!examen.getPreguntas().contains(pdb)) {
				//si no existe la eliminamos añadiendola al array para eliminar
				eliminadas.add(pdb);
			}
		});
		
		//ForEach para eliminar cada pregunta añadida a la lista
		eliminadas.forEach(p ->{
			examenDb.removePregunta(p);
		});
		
		//Agregamos o modificamos las preguntas agregadas en este editar
		examenDb.setPreguntas(examen.getPreguntas());
		
		return ResponseEntity.status(HttpStatus.CREATED).body(service.save(examenDb));
	}
	
	@GetMapping("/filtrar/{term}")
	public ResponseEntity<?> filtrar (@PathVariable String term){
		return ResponseEntity.ok(service.findByNombre(term));
	}
	
	@GetMapping("/asignaturas")
	public ResponseEntity<?> listarAsignaturas(){
		return ResponseEntity.ok(service.findAllAsignaturas());
	}
	
}
