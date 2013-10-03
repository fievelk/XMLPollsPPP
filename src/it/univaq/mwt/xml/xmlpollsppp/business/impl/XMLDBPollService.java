package it.univaq.mwt.xml.xmlpollsppp.business.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.springframework.stereotype.Service;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

import it.univaq.mwt.xml.xmlpollsppp.business.PollService;
import it.univaq.mwt.xml.xmlpollsppp.business.exceptions.RepositoryError;

@Service
public class XMLDBPollService implements PollService {

//	@Autowired
//	private DataSource dataSource;
	
	private final static String poll_ns = "http://it.univaq.mwt.xml/poll";
	Map<String, String> namespaces;
	Map<String, String> namespacesXslt;
	public static final String existDriver = "org.exist.xmldb.DatabaseImpl";
//	private static final String exist_uri_polls = "xmldb:exist://localhost:8085/exist/xmlrpc/db/xmlpollsppp/polls";
//	private static final String exist_uri_xslt = "xmldb:exist://localhost:8085/exist/xmlrpc/db/xmlpollsppp/xslt";
	public static final String exist_uri = "xmldb:exist://localhost:8085/exist/xmlrpc/db";
	
	private Collection dbRepository;
	private Collection dbPollsSkeletons;
	private Collection dbSubmittedPolls;
	private Collection dbXSLT;
	
	public XMLDBPollService() throws RepositoryError {
		
		try {
			namespaces = new HashMap();
			namespaces.put("p", poll_ns);
			
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
            
            //carichiamo i binding dei namespace nel servizio di query
//            System.out.println("NOMECOLLECTION "+coll.getName());
            
            // Se la collection nella quale si fa la query è dbXSLT, uso il namespace specifico
            if (coll.getName().equals(dbXSLT.getName())){
            	for (Entry<String, String> entry : namespacesXslt.entrySet()) {
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
//	                String resId = res.getDocumentId();
	                String resId = res.getId();
//	                System.out.println("RESID "+resId);
	                
	                XMLResource resWithXMLDeclaration = (XMLResource) dbPollsSkeletons.getResource(resId);
	                //collezioniamo le risorse come String
	                resultSkeletons.add(resWithXMLDeclaration.getContent().toString());
//	                System.out.println(resWithXMLDeclaration.getContent().toString());
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
	                
	                // Aggiungo code e title all'hasmap
	                codeTitle.put(code, title);
	            }
	        }
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
//		System.out.println("CODETITLE "+codeTitle);
		
		return codeTitle;
	}	    
    
    // XSLT QUERIES

    @Override
	public String getPollsXSLT() throws RepositoryError {
		
		String xslt = null;
        
		try {
			//prelevo il singolo xslt e lo converto in Stringa
			ResourceSet xsltResourceSet = queryDB("/xsl:stylesheet", dbXSLT);
			xslt = xsltResourceSet.getResource(0).getContent().toString(); // Al momento c'è un solo XSLT, poi andrà modificato
//			System.out.println(xslt);
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
            
		return xslt;
	}	    
    
}
