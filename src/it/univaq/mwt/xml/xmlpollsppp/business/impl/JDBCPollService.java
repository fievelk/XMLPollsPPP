package it.univaq.mwt.xml.xmlpollsppp.business.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.exist.xmldb.EXistResource;
import org.springframework.stereotype.Service;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

import it.univaq.mwt.xml.xmlpollsppp.business.PollService;
import it.univaq.mwt.xml.xmlpollsppp.business.exceptions.RepositoryError;
import it.univaq.mwt.xml.xmlpollsppp.business.model.Poll;

@Service
public class JDBCPollService implements PollService {

//	@Autowired
//	private DataSource dataSource;
	
	private final static String poll_ns = "http://it.univaq.mwt.xml/poll";
	static Map<String, String> namespaces;
//	public static final String existDriver = "org.exist.xmldb.DatabaseImpl";
	public static final String exist_uri = "xmldb:exist://localhost:8085/exist/xmlrpc/db/xmlpollsppp/polls";
	
	static {
		try {
			
            namespaces = new HashMap();
            namespaces.put("p", poll_ns);
            
			Class driver = Class.forName("org.exist.xmldb.DatabaseImpl");
			Database database = (Database) driver.newInstance();
			DatabaseManager.registerDatabase(database);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
	}
	
    //esegue una query XPath sul database
    private ResourceSet queryPollsSkeletonsDB(String xpath) throws RepositoryError {
    	Collection coll = null;
    	
        try {
        	coll = DatabaseManager.getCollection(exist_uri, "admin", "admin");
            XPathQueryService xpqs = (XPathQueryService)coll.getService("XPathQueryService", "1.0");
            
            //carichiamo i binding dei namespace nel servizio di query
            for (Entry<String, String> entry : namespaces.entrySet()) {
                xpqs.setNamespace(entry.getKey(), entry.getValue());
            }
            
            //eseguiamo la query e restituiamo i risultati
            ResourceSet result = xpqs.query(xpath);
            
            return result;
            
        } catch (XMLDBException ex) {
            throw new RepositoryError("ERRORE di accesso alla base di dati: " + ex.getMessage());
        }
    }	
    
	@Override
	public List<String> getAllPollsSkeletons() throws RepositoryError {

		List<String> titlesList = new ArrayList<String>();
		try {
	/*		
            col = DatabaseManager.getCollection(exist_uri, "admin", "admin");
            
            //services are requested for special tasks such as querying a collection with XPath, or managing a collection.
            XPathQueryService xpqs = (XPathQueryService)col.getService("XPathQueryService", "1.0");  
            
            //carichiamo i binding dei namespace nel servizio di query
            for (Entry<String, String> entry : namespaces.entrySet()) {
                xpqs.setNamespace(entry.getKey(), entry.getValue());
            }
            
            xpqs.setProperty("indent", "yes");
            ResourceSet result = xpqs.query*/
			
			ResourceSet result = queryPollsSkeletonsDB("/p:poll/p:pollHead/p:title/text()");
            ResourceIterator i = result.getIterator();
            Resource res = null;
            
            while(i.hasMoreResources()) {
                try {
                    res = i.nextResource();
                    titlesList.add(res.getContent().toString());
                } finally {
                    //dont forget to cleanup resources
                    try { ((EXistResource)res).freeResources(); } catch(XMLDBException xe) {xe.printStackTrace();}
                }
            }
            
        } catch (XMLDBException e) {
			e.printStackTrace();
		} finally {
            //dont forget to cleanup
            if(col != null) {
                try { 
                	col.close(); 
                } catch(XMLDBException xe) {
                	xe.printStackTrace();
                	}
                }
        	}
		
        return titlesList;
	}
	    
	
/*	@Override
	public List<Poll> getAllPollsSkeletons() throws RepositoryError {

		Collection col = null;
        XMLResource res = null;
		
		try {
            //acquisiamo un riferimento alla root collection fornendo le credenziali di accesso
			
            col = DatabaseManager.getCollection(exist_uri, "admin", "admin");
//            res = (XMLResource)col.getResource("poll.xml");
            
            if(res == null) {
                System.out.println("document not found!");
            } else {
                System.out.println(res.getContent());
            }
            
        } catch (XMLDBException ex) {
            throw new RepositoryError("ERRORE di creazione della base di dati: " + ex.getMessage());
        }
		
		List<String> pidlist = new ArrayList<String>();
		
		try {
            //costruiamo una query estraendo i metadati (cioè le risorse che abbiamo
            //precedentemente inserito) corrispondenti a un certo filtro
            ResourceSet polls = queryDatabase("/p:poll", col);
            if (polls.getSize() > 0) {
                //iteriamo tra i risultati
                ResourceIterator it = polls.getIterator();
                while (it.hasMoreResources()) {
                    //preleviamo il singolo risultato e le convertiamo in risorsa xml, poichè sappiamo cosa stiamo estraendo
                    XMLResource resXML = (XMLResource) it.nextResource();
                    //collezioniamo gli id delle risorse così ottenuti
                    pidlist.add(resXML.getId());
                }
            }
        } catch (XMLDBException ex) {
            throw new RepositoryError("ERRORE di accesso alla base di dati: " + ex.getMessage());
        }
        
        return null;
	}
	*/
	@Override
	public List<Poll> getAllSubmittedPolls() {
		// TODO Auto-generated method stub
		return null;
	}

}
