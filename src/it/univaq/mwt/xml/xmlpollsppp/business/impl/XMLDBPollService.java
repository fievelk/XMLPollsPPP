package it.univaq.mwt.xml.xmlpollsppp.business.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.exist.xmldb.EXistResource;
import org.springframework.stereotype.Service;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

import it.univaq.mwt.xml.xmlpollsppp.business.PollService;
import it.univaq.mwt.xml.xmlpollsppp.business.exceptions.RepositoryError;
import it.univaq.mwt.xml.xmlpollsppp.business.model.Option;
import it.univaq.mwt.xml.xmlpollsppp.business.model.Poll;
import it.univaq.mwt.xml.xmlpollsppp.business.model.Question;

@Service
public class XMLDBPollService implements PollService {

	private final static String poll_ns = "http://it.univaq.mwt.xml/poll";
	private final static String submittedPoll_ns = "http://it.univaq.mwt.xml/submittedpoll";
	Map<String, String> namespaces;
	Map<String, String> namespacesSubmittedPolls;
	Map<String, String> namespacesXslt;
	public static final String existDriver = "org.exist.xmldb.DatabaseImpl";
	public static final String exist_uri = "xmldb:exist://localhost:8085/exist/xmlrpc/db";
	
	private Collection dbRepository;
	private Collection dbPollsSkeletons;
	private Collection dbSubmittedPolls;
	private Collection dbXSLT;
	
	public XMLDBPollService() throws RepositoryError {
		
		try {
			namespaces = new HashMap();
			namespaces.put("p", poll_ns);
			
			namespacesSubmittedPolls = new HashMap();
			namespacesSubmittedPolls.put("p", submittedPoll_ns);
			
			namespacesXslt = new HashMap();
			namespacesXslt.put("xsl", "http://www.w3.org/1999/XSL/Transform");
			
			Class driver = Class.forName(existDriver);
			Database database = (Database) driver.newInstance();
			DatabaseManager.registerDatabase(database);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		checkDatabase();
	}

    
    private void checkDatabase() throws RepositoryError {
        try {
            //acquisiamo un riferimento alla root collection fornendo le credenziali di accesso
            Collection root = DatabaseManager.getCollection(exist_uri, "admin", "admin");
            //creiamo o acquisiamo un riferimento a una sotto-collection:
            //prima di tutto acquisiamo il collection management service relativo alla collection nella quale lavorare...
            CollectionManagementService cms = (CollectionManagementService) root.getService("CollectionManagementService", "1.0");
            //quindi creiamo la sotto-collection
            dbRepository = cms.createCollection("xmlpollsppp");

            //creiamo altre tre sotto-collection in quella appena creata
            cms = (CollectionManagementService) dbRepository.getService("CollectionManagementService", "1.0");
            dbPollsSkeletons = cms.createCollection("pollsSkeletons");
            dbSubmittedPolls = cms.createCollection("submittedPolls");
            dbXSLT = cms.createCollection("xsltPolls");

        } catch (XMLDBException ex) {
            throw new RepositoryError("ERRORE di creazione della base di dati: " + ex.getMessage());
        }
    }    
    
    // La coll passata come parametro dev'essere una di quelle dichiarate nel costruttore iniziale e create in checkDatabase()
    private ResourceSet queryDB(String xpath, Collection coll) throws RepositoryError {
    	
        try {
            XPathQueryService xpqs = (XPathQueryService)coll.getService("XPathQueryService", "1.0");
            // Se la collection nella quale si fa la query è dbXSLT, uso il namespace specifico
            if (coll.getName().equals(dbXSLT.getName())){
            	for (Entry<String, String> entry : namespacesXslt.entrySet()) {
                    xpqs.setNamespace(entry.getKey(), entry.getValue());
                }
            } else if (coll.getName().equals(dbSubmittedPolls.getName())){
            	for (Entry<String, String> entry : namespacesSubmittedPolls.entrySet()) {
            		xpqs.setNamespace(entry.getKey(), entry.getValue());
                }
            } else {
                for (Entry<String, String> entry : namespaces.entrySet()) {
                    xpqs.setNamespace(entry.getKey(), entry.getValue());            	
                }
            }    
            //eseguiamo la query e restituiamo i risultati
            ResourceSet result = xpqs.query(xpath);
            return result;
            
        } catch (XMLDBException ex) {
            throw new RepositoryError("ERRORE di accesso alla base di dati: " + ex.getMessage());
        }
    }	
    

    // Restituisce lo skeleton completo di dichiarazione xml
    @Override
	public List<String> getPollSkeletonBy(String criteria) throws RepositoryError {
    	List<String> resultSkeletons = new ArrayList<String>();

    	try{
			ResourceSet pollsSkeletons = queryDB("/p:poll[.//" + criteria + "]", dbPollsSkeletons);

			if (pollsSkeletons.getSize() > 0) {
	        	ResourceIterator it = pollsSkeletons.getIterator();
	            while (it.hasMoreResources()) {
	                //preleviamo il singolo risultato e le convertiamo in risorsa xml, poichè sappiamo cosa stiamo estraendo
	                XMLResource res = (XMLResource) it.nextResource();
	                // Prendo l'id del documento specifico
	                String resId = res.getId();
	                XMLResource resWithXMLDeclaration = (XMLResource) dbPollsSkeletons.getResource(resId);
	                //collezioniamo le risorse come String
	                resultSkeletons.add(resWithXMLDeclaration.getContent().toString());
	            }
	        }
    	} catch (XMLDBException ex) {
            throw new RepositoryError("ERRORE di accesso alla base di dati: " + ex.getMessage());
        }
		return resultSkeletons;
    }
    
    
	@Override
	public String getPollSkeletonByCode(String code) throws RepositoryError {
		return getPollSkeletonBy("p:pollHead/p:code='" + code + "'").get(0);
	}
	

	@Override
	public TreeMap<String, String> getAllPollsCodeAndTitle() throws RepositoryError {
		
		TreeMap<String, String> codeTitle = new TreeMap<String, String>();
		try {
            ResourceSet codeResSet = queryDB("/p:poll/p:pollHead/p:code/text()", dbPollsSkeletons);
            
			if (codeResSet.getSize() > 0) {
	            ResourceIterator it = codeResSet.getIterator();
	            while (it.hasMoreResources()) {
	                //prelevo il singolo code e lo converto in String
	                String code = it.nextResource().getContent().toString();
	                // Prelevo il titolo relativo al codice e lo converto in String
//	                XMLResource titleRes = (XMLResource) xpqs.query("/p:poll/p:pollHead[p:code='" + code + "']/p:title/text()").getResource(0);
	                ResourceSet resSet = queryDB("/p:poll/p:pollHead[p:code='" + code + "']/p:title/text()",dbPollsSkeletons);
	                XMLResource titleRes = (XMLResource) resSet.getResource(0);
	                String title = titleRes.getContent().toString();
	                // Aggiungo code e title all'hashmap
	                codeTitle.put(code, title);
	            }
	        }
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
		return codeTitle;
	}	    
    
	
	@Override
	public String createSubmittedPoll(String submittedPoll) throws RepositoryError {

        Collection col = null;
        XMLResource res = null;
        try { 
            col = dbSubmittedPolls;
            // Crea una nuova XMLResource, a cui sarà assegnato un nuovo ID
            String seqId = col.createId();
            res = (XMLResource)col.createResource("submittedPoll"+seqId, "XMLResource");
            
            res.setContent(submittedPoll);
            System.out.print("storing document " + res.getId() + "...");
            col.storeResource(res);
            System.out.println("ok.");
        } catch (XMLDBException e) {
			e.printStackTrace();
		} finally {
            // Libera le risorse
            if(res != null) {
                try { ((EXistResource)res).freeResources(); } catch(XMLDBException xe) {xe.printStackTrace();}
            }
            
            if(col != null) {
                try { col.close(); } catch(XMLDBException xe) {xe.printStackTrace();}
            }
        }
		
		
		return null;
	}
	
	
	@Override
	public List<Option> getPollAnswersStats(int pollCode, String questionCode) throws RepositoryError {
		
		List<Option> optionsList = new ArrayList<Option>();
		
		try {
            ResourceSet optionsResSet = queryDB("/p:poll[p:pollHead/p:code='"+pollCode+"']//p:option[starts-with(@code,'"+questionCode+"')]", dbPollsSkeletons);

            if (optionsResSet.getSize() > 0) {
				// Per ogni OPZIONE possibile prendo il codice, il testo e il numero di volte in cui ricorre nei submittedPolls
	            ResourceIterator it = optionsResSet.getIterator();
	            while (it.hasMoreResources()) {
	                // Prelevo la singola option xml e la converto in String. In seguito ne estraggo manualmente il code e il testo 
	            	// tramite regex. Devo prelevare tutto il codice perché il code non può essere serializzato senza il suo parent element
	                // This is compliant with the XQuery specification: you can query for an attribute, but you are not allowed to serialize 
	            	// it. An attribute always needs to be attached to an element when serialized
	                XMLResource optionRes = (XMLResource) it.nextResource();
	                String optionString = optionRes.getContent().toString();
	                
	                // Prelevo il contenuto testuale
	                Pattern pattern = Pattern.compile("(?<=>).*(?=</option>)");
	                Matcher matcher = pattern.matcher(optionString);
	                String optionContent = null;
	                if (matcher.find()) {
	                	optionContent = matcher.group(0);
	                }
	                
	                // Prelevo il code
	                pattern = Pattern.compile("(?<=code=\").*?(?=\")");
	                matcher = pattern.matcher(optionString);
	                String optionCode = null;
	                if (matcher.find()) {
	                	optionCode = matcher.group(0);
	                }
	                
	                // Conto quante volte quella opzione è stata scelta come risposta
	                ResourceSet countResSet = queryDB("count(/p:submittedPoll[p:pollHead/p:code='"+pollCode+"']//p:answer[starts-with(@code,'"+optionCode+"')])", dbSubmittedPolls);
	                XMLResource countRes = (XMLResource) countResSet.getResource(0);
	                String count = countRes.getContent().toString();
	                BigDecimal countBD = new BigDecimal(count);
	                
	                // Creo l'Option e l'aggiungo alla lista optionsList
	                Option option = new Option(optionCode, optionContent, countBD);
	                optionsList.add(option);
	            }
	            
	        }
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
		return optionsList;
	}
	
	
	@Override
	public Poll getPollInfos(int pollCode) throws RepositoryError {
		List<Question> questionsList = new ArrayList<Question>();
		List<Question> nonRequiredQuestions = new ArrayList<Question>();
		Poll poll = new Poll();
		
		try {
			ResourceSet submissionsCountResSet = queryDB("count(/p:submittedPoll[p:pollHead/p:code='"+pollCode+"'])", dbSubmittedPolls);
			XMLResource submissionsCountRes = (XMLResource) submissionsCountResSet.getResource(0);
			BigDecimal submissionsCount = new BigDecimal(submissionsCountRes.getContent().toString());
			poll.setPollSubmissions(submissionsCount);
			
            ResourceSet questionsResSet = queryDB("/p:poll[p:pollHead/p:code='"+pollCode+"']//p:question", dbPollsSkeletons);
            
            // Prendo tutto l'elemento option, lo serializzo e ne estraggo la substring dopo "code".
            // This is compliant with the XQuery specification: you can query for an attribute, but you are not allowed to serialize it.
            // An attribute always needs to be attached to an element when serialized
            
			if (questionsResSet.getSize() > 0) {
				// Per ogni QUESTION possibile prendo il codice e il testo
	            ResourceIterator it = questionsResSet.getIterator();
	            while (it.hasMoreResources()) {
	                // Prelevo la singola question xml e la converto in String. In seguito ne estraggo manualmente il code e il testo 
	            	// tramite regex. Devo prelevare tutto il codice perché il code non può essere serializzato senza il suo parent element
	            	Question question = new Question();
	            	XMLResource questionRes = (XMLResource) it.nextResource();
	                String questionString = questionRes.getContent().toString();

	                // Prelevo il contenuto testuale
	                Pattern pattern = Pattern.compile("(?<=>).*(?=</question>)");
	                Matcher matcher = pattern.matcher(questionString);
	                String questionContent = null;
	                if (matcher.find()) {
	                	questionContent = matcher.group(0);
	                }
	                // Prelevo il code
	                pattern = Pattern.compile("(?<=code=\").*?(?=\")");
	                matcher = pattern.matcher(questionString);
	                String questionCode = null;
	                if (matcher.find()) {
	                	questionCode = matcher.group(0);
	                }
	                // Controllo se la question sia required o meno
	                pattern = Pattern.compile("(?<=required=\")true?(?=\")");
	                matcher = pattern.matcher(questionString);
	                String questionRequired = null;
	                if (matcher.find()) {
	                	questionRequired = matcher.group(0);
	                	question.setRequired(true);
	                } else {
	                	nonRequiredQuestions.add(question);
	                }
	                // Creo la Question e l'aggiungo alla lista optionsList
	                question.setCode(questionCode);
	                question.setContent(questionContent);
	                questionsList.add(question);
	            }
	            
	            poll.setQuestions(questionsList);
	            BigDecimal submissionsWithOptAnswer = getOptionalSubmissionCount(pollCode);
	            poll.setSubmissionsWithNonReqAnswer(submissionsWithOptAnswer);
	        }
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
		return poll;
	}
	
	
	private BigDecimal getOptionalSubmissionCount(int pollCode) throws RepositoryError{
		// Devo farmi restituire il numero di poll che abbiano ALMENO una risposta a una domanda opzionale.
		ResourceSet nonReqSubmissionsCRSet = queryDB("count(/p:submittedPoll[.//p:code='"+pollCode+"' and .//p:answer[preceding-sibling::p:question[1][not(@required) or (@required='false')]]])", dbSubmittedPolls);
		BigDecimal submissionsWithOptAnswer = null;
		try {
			Resource nonReqSubmission = nonReqSubmissionsCRSet.getResource(0);
			submissionsWithOptAnswer = new BigDecimal(nonReqSubmission.getContent().toString());
		} catch (XMLDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return submissionsWithOptAnswer;
		
	}

    // XSLT QUERIES

    @Override
	public String getPollsXSLT() throws RepositoryError {
		String xslt = null;
		try {
			//prelevo il singolo xslt e lo converto in Stringa
			ResourceSet xsltResourceSet = queryDB("/xsl:stylesheet", dbXSLT);
			xslt = xsltResourceSet.getResource(0).getContent().toString(); // Al momento c'è un solo XSLT, poi andrà modificato
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
		return xslt;
	}
}
