package com.company;

import com.clarkparsia.owlapiv3.OWL;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class OntoUtils {
    public OntoUtils() {
    }

    public static OWLOntology openOntology(String ontoPath) {
        String path = checkOntoPath(ontoPath);
        IRI physicalURI = IRI.create(path);
        OWLOntology ontology = null;

        try {
            ontology = OWL.manager.loadOntology(physicalURI);
        } catch (OWLOntologyCreationException var5) {
            var5.printStackTrace();
        }

        return ontology;
    }

    public static String checkOntoPath(String ontoPath) {
        String path = ontoPath;
        if (!ontoPath.startsWith("http:") && !ontoPath.startsWith("https:") && !ontoPath.startsWith("ftp:") && !ontoPath.startsWith("file:")) {
            path = "file:" + ontoPath;
        }

        return path;
    }

    public static boolean isConsistent(OWLOntology o) {
        return false;
    }
}