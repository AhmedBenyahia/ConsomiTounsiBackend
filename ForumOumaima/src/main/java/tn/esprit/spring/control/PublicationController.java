package tn.esprit.spring.control;

import java.util.List;
import java.util.NoSuchElementException;

import javax.validation.Valid;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import tn.esprit.spring.entity.Comment;
import tn.esprit.spring.entity.Publication;
import tn.esprit.spring.entity.RateCom;
import tn.esprit.spring.entity.RatePub;
import tn.esprit.spring.exception.RedundantPublicationException;
import tn.esprit.spring.service.IPublicationService;



@AllArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/publication")
public class PublicationController {
	
	private final IPublicationService publicationService;
	
	@GetMapping("/getPubById/{id}")
	public Publication getPubById(@PathVariable("id") Long id) {
		return publicationService.getById(id);
	}
	@GetMapping("/getPubBYCategorie/{id}")
	public List <Publication> getPubByCategorie(@PathVariable("id") Long id){
		return publicationService.getPubBycategorie(id);
	}
	 @GetMapping
	 public List<Publication> getAllPublication(){
		 return publicationService.getAllPublication();
	 }
	@PostMapping("/create")
	public Publication create(@RequestBody @Valid Publication publication) {
		publicationService.createOrUpdate(publication);
	return publication;
	}

	@PutMapping("/update")
	public Publication update(@RequestBody @Valid Publication publication) {
		if (publication.getId()==null) {
			throw new NoSuchElementException();
		}
		return publicationService.createOrUpdate(publication);
	}
	
	
	@DeleteMapping("/delete/{id}")
	public void delete(@PathVariable("id") Long id) {
		publicationService.delete(id);
	}
	
    @GetMapping("/find-user-publication/{id}")
    public List<Publication> findByUserId(@PathVariable("id")Long id){
		return publicationService.findByUserId(id);
    	
    }
    @GetMapping("/rate/{idPublication}/{idUser}/{rate}")
	public Publication rate(@PathVariable("idPublication")Long idPublication,
			@PathVariable("idUser")Long idUser,
			@PathVariable("rate")RatePub rate) {
		return publicationService.rate(idPublication,idUser,rate);
		
	}
    @ExceptionHandler({ NoSuchElementException.class })
    public ResponseEntity handleException() {
      return new ResponseEntity<>("There's no publication with such ID", HttpStatus.NOT_FOUND) ; 
    }
    
    @GetMapping("/new")
   public List<Publication> news(){
    	
    	return publicationService.news();
    }
    
    @PostMapping("/uploadFiles/{idPub}")
	public void save(@RequestParam("uploadedFiles") MultipartFile[] uploadedFiles,
			@PathVariable Long idPub) {

		publicationService.save(uploadedFiles,idPub);
	}
    @ExceptionHandler({ RedundantPublicationException.class })
    public ResponseEntity<String> handleRedundantException() {
      return new ResponseEntity<>("The publication is redundant", HttpStatus.NOT_ACCEPTABLE) ;
    }
    
    @GetMapping("/suggestion/{userId}")
    public List<Publication> suggestions(@PathVariable Long userId){

     	return publicationService.suggestion(userId);
     }
    
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity handle(MethodArgumentNotValidException exception) {
	    List<ObjectError> violations = exception.getAllErrors();
	    String errorMessage = "";
	    if (!violations.isEmpty()) {
	        StringBuilder builder = new StringBuilder();
	        violations.forEach(violation -> builder.append(" " + violation.getDefaultMessage()));
	        errorMessage = builder.toString();
	    } else {
	        errorMessage = "ConstraintViolationException occured.";
	    }
	    return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
	 }
}
