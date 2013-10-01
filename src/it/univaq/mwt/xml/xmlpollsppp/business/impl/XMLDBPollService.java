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
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

import it.univaq.mwt.xml.xmlpollsppp.business.PollService;
import it.univaq.mwt.xml.xmlpollsppp.business.exceptions.RepositoryError;

@Service
public class XMLDBPollService implements PollService {

//	@Autowired
//	private DataSource dataSource;
	
	private final static String poll_ns = "http://it.univaq.mwt.xml/poll";
	static Map<String, String> namespaces;
//	public static final String existDriver = "org.exist.xmldb.DatabaseImpl";
	public static final String exist_uri_polls = "xmldb:exist://localhost:8085/exist/xmlrpc/db/xmlpollsppp/polls";
	public static final String exist_uri_xslt = "xmldb:exist://localhost:8085/exist/xmlrpc/db/xmlpollsppp/xslt";
	
	static {
            namespaces = new HashMap();
            namespaces.put("p", poll_ns);
            
        try {    
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
	
    //esegue una query XPath sul database degi Polls Skeletons
    private ResourceSet queryPollsSkeletonsDB(String xpath) throws RepositoryError {
    	Collection coll = null;
    	
        try {
        	coll = DatabaseManager.getCollection(exist_uri_polls, "admin", "admin");
//        	System.out.println("NOME COLLECTION "+coll.getName());
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
    
 /* ZONA ORDINATA */
	
    @Override
	public List<String> getPollSkeletonBy(String criteria) throws RepositoryError {
    	
    	List<String> resultSkeletons = new ArrayList<String>();

    	try{
			ResourceSet pollsSkeletons = queryPollsSkeletonsDB("/p:poll[.//" + criteria + "]"); //AGGIUNGI I CRITERI

			if (pollsSkeletons.getSize() > 0) {
	        	ResourceIterator it = pollsSkeletons.getIterator();
	            while (it.hasMoreResources()) {
	                //preleviamo il singolo risultato e le convertiamo in risorsa xml, poichè sappiamo cosa stiamo estraendo
	                XMLResource res = (XMLResource) it.nextResource();
	                //collezioniamo le risorse come String
	                resultSkeletons.add(res.getContent().toString());
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
		Collection coll = null;
		try {
			/*coll = DatabaseManager.getCollection(exist_uri, "admin", "admin");

			XPathQueryService xpqs = (XPathQueryService)coll.getService("XPathQueryService", "1.0");
			
			//carico i binding dei namespace nel servizio di query
            for (Entry<String, String> entry : namespaces.entrySet()) {
                xpqs.setNamespace(entry.getKey(), entry.getValue());
            }*/
            
//            ResourceSet codeResSet = xpqs.query("/p:poll/p:pollHead/p:code/text()");
            ResourceSet codeResSet = queryPollsSkeletonsDB("/p:poll/p:pollHead/p:code/text()");
            
            
			if (codeResSet.getSize() > 0) {
	            ResourceIterator it = codeResSet.getIterator();
	            while (it.hasMoreResources()) {
	                //prelevo il singolo code e lo converto in String
	                String code = it.nextResource().getContent().toString();
	                
	                // Prelevo il titolo relativo al codice e lo converto in String
//	                XMLResource titleRes = (XMLResource) xpqs.query("/p:poll/p:pollHead[p:code='" + code + "']/p:title/text()").getResource(0);
	                ResourceSet resSet = queryPollsSkeletonsDB("/p:poll/p:pollHead[p:code='" + code + "']/p:title/text()");
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
	
	/* Query sul db delle trasformazioni XSLT */
	
    //esegue una query XPath sul database
    private ResourceSet queryPollsXSLTDB(String xpath) throws RepositoryError {
    	Collection coll = null;
    	namespaces.put("xsl", "http://www.w3.org/1999/XSL/Transform");
    	
        try {
        	coll = DatabaseManager.getCollection(exist_uri_xslt, "admin", "admin");
//        	System.out.println("NOME COLLECTION "+coll.getName());
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
	public String getPollsXSLT() throws RepositoryError {
		
		String xslt = null;
		
        //prelevo il singolo xslt e lo converto in XMLResource
		try {
			ResourceSet xsltResourceSet = queryPollsXSLTDB("/xsl:stylesheet");
			System.out.println(xsltResourceSet);
			System.out.println("getResource "+ xsltResourceSet.getResource(0));
			xslt = xsltResourceSet.getResource(0).getContent().toString();
		} catch (XMLDBException e) {
			e.printStackTrace();
		}
            
		System.out.println("XSLT: "+xslt);
		
		return xslt;
	}	    

}
