package tn.esprit.spring.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import tn.esprit.spring.entity.CategoriePublication;
import tn.esprit.spring.entity.History;
import tn.esprit.spring.entity.Publication;
import tn.esprit.spring.entity.RatePub;
import tn.esprit.spring.entity.User;
import tn.esprit.spring.exception.RedundantPublicationException;
import tn.esprit.spring.repository.IPublicationRepository;
import tn.esprit.spring.service.IHistoryService;
import tn.esprit.spring.service.IPublicationService;
import tn.esprit.spring.service.IUserService;

@AllArgsConstructor
@Service
public class PublicationServiceImpl implements IPublicationService{
	private final IPublicationRepository publicationRepository;
	private final Environment environment;
	private final  IUserService iUserService;
	private final IHistoryService iHistoryService;

	@Override
	public Publication createOrUpdate(Publication publication) {
		if (publication.getId()!=null) 
		{
				getById(publication.getId());
				
		}
		else {
			if(checkForRedundantPublication(publication)) {
				throw new RedundantPublicationException("the publication is redundant");
			}
		};
		publication.setDatePublication(LocalDateTime.now());
		
		
		return publicationRepository.save(publication);
	}

	@Override
	public void delete(Long id) {
		publicationRepository.deleteById(id);
		
	}

	@Override
	public Publication getById(Long id) throws NoSuchElementException {
		Optional<Publication> publication = publicationRepository.findById(id);
		if (publication.isPresent()) return publication.get();
		throw new NoSuchElementException();
	}

	@Override
	public List<Publication> findByUserId(Long id) {
		System.out.println(id);
		return publicationRepository.findByUserId(id).orElse(new ArrayList());
	}
	
	
	@Override
	public Publication rate(Long idPublication, Long idUser, RatePub rate) {
		Publication publication= getById(idPublication);
		 RatePub userRate = publication.getRatingPub().get(idUser);
		 if (userRate != null) {
				if(userRate==RatePub.VERY_EXCELLENT){
						 publication.setScore(publication.getScore()-5);
				    }
				else if (userRate==RatePub.EXCELLENT) 
					{
						 publication.setScore((float) (publication.getScore()-4.5));
					 }
				 else if (userRate==RatePub.VERY_GOOD)  
					 {
						 publication.setScore((float) (publication.getScore()-4));
					 }
				 else if (userRate==RatePub.GOOD)  
					 {
						 publication.setScore((float) (publication.getScore()-3.5));
					 }
			    
			     else if (userRate==RatePub.ABOVE_AVERAGE) 
				      {
					     publication.setScore((float) (publication.getScore()-3));
				      }
		    	 else if (userRate==RatePub.AVERAGE) 
			    	  {  
			    		 publication.setScore((float) (publication.getScore()-2.5));
			          }
				 else if (userRate==RatePub.POOR) 
					   {
						  publication.setScore((float) (publication.getScore()-2));
			           }
				  else if (userRate==RatePub.VERY_POOR) 
					    {
						   publication.setScore((float) (publication.getScore()-1.5));
			            }
			      else if (userRate==RatePub.BAD) 
					    {
						    publication.setScore((float) (publication.getScore()-1));  
					    }
			      else 
					    {
						    publication.setScore((float) (publication.getScore()-0.5));
					    }
				 }
		 
		 if(rate==RatePub.VERY_EXCELLENT) 
			 { 
				 publication.setScore(publication.getScore()+5); 
			 }
		 else if (rate==RatePub.EXCELLENT) 
			{
				 publication.setScore((float) (publication.getScore()+4.5));
			 }
		 else if (rate==RatePub.VERY_GOOD)  
			 {
				 publication.setScore((float) (publication.getScore()+4));
			 }
		 else if (rate==RatePub.GOOD)  
			 {
				 publication.setScore((float) (publication.getScore()+3.5));
			 }
	    
	     else if (rate==RatePub.ABOVE_AVERAGE) 
		      {
			     publication.setScore((float) (publication.getScore()+3));
		      }
    	 else if (rate==RatePub.AVERAGE) 
	    	  {  
	    		 publication.setScore((float) (publication.getScore()+2.5));
	          }
		 else if (rate==RatePub.POOR) 
			   {
				  publication.setScore((float) (publication.getScore()+2));
	           }
		  else if (rate==RatePub.VERY_POOR) 
			    {
				   publication.setScore((float) (publication.getScore()+1.5));
	            }
	      else if (rate==RatePub.BAD) 
			    {
				    publication.setScore((float) (publication.getScore()+1));  
			    }
	      else 
			    {
				    publication.setScore((float) (publication.getScore()+0.5));
			    }
     	  
		 publication.getRatingPub().put(idUser, rate);
		 publication = publicationRepository.save(publication);
			History history = new History(publication.getUser(), rate, publication);
			this.iHistoryService.createOrUpdate(history);
			return publication;
		
	}

	@Override
	public List<Publication> news() {
		
		return publicationRepository.findFirst5ByOrderByDatePublicationDesc();
	}
	//@Scheduled(fixedDelay = 10000)
	//@Scheduled(cron = "0 */3 * * *")
	//@Scheduled(cron = "0 0 */3 * * *")
		private void removeUnactiveComment() {
		List<Publication> pubs = publicationRepository.affichNotNullPublication();
		pubs.forEach(entity -> publicationRepository.delete(entity));
	}

	
	@Override
	public void save(MultipartFile[] files,Long idPub) {
		if (files != null) {
		Publication publication = getById(idPub);
			for (MultipartFile multipartFile : files) {
			File file = fileConverter(multipartFile,
						environment.getProperty("spring.servlet.multipart.location")
						);
			publication.setPicture(file.getName());
			createOrUpdate(publication);
			
			}

		}

	}
	
	
	private static File fileConverter(MultipartFile file, String basePath) {

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");
		String fileName= basePath + "/" + now.format(formatter) + "_" + file.getOriginalFilename();
		File convertedFile =
				new File(fileName);
		FileOutputStream fileOutputStream = null;
		try {
			boolean createdFile = convertedFile.createNewFile();
			if (createdFile) {
				fileOutputStream = new FileOutputStream(convertedFile);
				fileOutputStream.write(file.getBytes());
				fileOutputStream.close();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return convertedFile;
	}
		
	private boolean checkForRedundantPublication(Publication publication) {
		List<Publication> pubs = publicationRepository
				.findByTitleContainsOrContentContains(publication.getTitle().trim(), publication.getContent().trim());

		String firstHalfOfContent = publication.getContent().substring(0, publication.getContent().length() / 2 + 1);
		String secondHalfOfContent = publication.getContent().substring(publication.getContent().length() / 2);

		pubs.addAll(publicationRepository
				.findByTitleContainsOrContentContains(publication.getTitle().trim(), firstHalfOfContent.trim()));

		pubs.addAll(publicationRepository
				.findByTitleContainsOrContentContains(publication.getTitle().trim(), secondHalfOfContent.trim()));

		return pubs.size() > 0;
	}
	
	@Override
	public List<Publication> suggestion(Long idUser) {
		User user = this.iUserService.getById(idUser);
		Set<CategoriePublication> favCategory = this.iHistoryService.getUserFavCategories(user);
		List<Publication> suggestedPubs = new ArrayList<>();
		for (CategoriePublication categoriePub : favCategory) {
			suggestedPubs.addAll(this.publicationRepository.findByCategoriePublicationsOrderByScoreDesc(categoriePub));
			if (suggestedPubs.size() >= 10) {
				break;
			}
		}
		return suggestedPubs.stream()
				.sorted(Comparator.comparing(Publication::getScore).reversed())
				.collect(Collectors.toList());
	}
	
	public List<Publication> getAllPublication(){
		return publicationRepository.findAll();
	}
	
	public List<Publication> getPubBycategorie(Long id){
		CategoriePublication categoriePublication = new CategoriePublication();
	 categoriePublication.setId(id);
	return publicationRepository.findByCategoriePublicationsOrderByScoreDesc(categoriePublication);
	}
}

