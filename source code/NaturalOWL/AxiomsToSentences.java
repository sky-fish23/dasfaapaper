package edu.njupt.radon.exp.as2022;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;

import com.clarkparsia.owlapiv3.OWL;

import edu.njupt.radon.utils.OWLTools;
import edu.njupt.radon.utils.io.PrintStreamObject;

public class AxiomsToSentences {

    public static void main(String[] args) throws Exception {
    	String ontoName = "Geography";        	
        String ontoRoot = "as2022/onto/";           
        String sentsPath = "onto/nlp/"+ontoName+"_sentencesList.txt";
        String axiomsPath = "onto/nlp/"+ontoName+"_axiomList.txt";
        
        Vector<OWLAxiom> axioms = new Vector<OWLAxiom>();
        OWLOntology o = OWL.manager.loadOntology(IRI.create("file:" + ontoRoot + ontoName+".owl"));
        PrintStream console = System.out;

        System.setOut((new PrintStreamObject(sentsPath)).ps);          
        long st = System.currentTimeMillis();
        AxiomsToSentences transf = new AxiomsToSentences();       
        for(OWLAxiom ax: o.getLogicalAxioms()) {
            axioms.add(ax);
            /*if(ax.toString().contains("Animal") && ax.toString().contains("ObjectExactCardinality")) {
            	System.out.println("stop");
            }*/
            transf.printAxiomInfo(ax);
        }
        console.print("\n"+(System.currentTimeMillis()-st)+"\t");
        
        System.setOut((new PrintStreamObject(axiomsPath)).ps);	 
        for(int i =0; i<axioms.size(); i++) {
        	System.out.print(axioms.get(i)+"\n");
        }
    }
    
   
    public void printAxiomInfo(OWLAxiom a) {
        //NatrualOWL
        if (a instanceof OWLSubClassOfAxiom) {
            OWLSubClassOfAxiom subAxiom = (OWLSubClassOfAxiom) a;
            OWLClassExpression suboce = subAxiom.getSubClass();
            OWLClassExpression supoce = subAxiom.getSuperClass();
            // By default, the subconcept in a subsumption is atomic
            //printConcept(suboce);
            this.printSubsumption(suboce, supoce);
            
        } else {
            if (a instanceof OWLObjectPropertyDomainAxiom) {
                OWLObjectPropertyDomainAxiom domainAxiom = (OWLObjectPropertyDomainAxiom) a;
                this.printObjectProperty(domainAxiom.getProperty());
                System.out.print(" has domain ");
                printConcept(domainAxiom.getDomain());
            } else if (a instanceof OWLDataPropertyDomainAxiom) {
                OWLDataPropertyDomainAxiom domainAxiom = (OWLDataPropertyDomainAxiom) a;
                this.printDataProperty(domainAxiom.getProperty());
                System.out.print(" has domain ");
                printConcept(domainAxiom.getDomain());
            } else if (a instanceof OWLObjectPropertyRangeAxiom) {
                OWLObjectPropertyRangeAxiom rangeAxiom = (OWLObjectPropertyRangeAxiom) a;
                this.printObjectProperty(rangeAxiom.getProperty());
                System.out.print(" has range ");
                printConcept( rangeAxiom.getRange());
            } else if (a instanceof OWLDataPropertyRangeAxiom) {
                OWLDataPropertyRangeAxiom rangeAxiom = (OWLDataPropertyRangeAxiom) a;
                this.printDataProperty(rangeAxiom.getProperty());
                System.out.print(" has range " + ((OWLDataRange) rangeAxiom.getRange()).toString());
            } else if (a instanceof OWLDisjointClassesAxiom) {
                OWLDisjointClassesAxiom disjAxiom = (OWLDisjointClassesAxiom) a;
                //System.out.print(" Disjoint classes: (");
                Iterator var18 = disjAxiom.getClassExpressions().iterator();
                while (var18.hasNext()) {
                    OWLClassExpression oce1 = (OWLClassExpression) var18.next();
                    printConcept(oce1);
                    if(var18.hasNext())
                        System.out.print(" isn't a kind of ");
                }
            } else if (a instanceof OWLDisjointObjectPropertiesAxiom) {
            	OWLDisjointObjectPropertiesAxiom disjAxiom = (OWLDisjointObjectPropertiesAxiom) a;
                //System.out.print(" Disjoint classes: (");
                Iterator var18 = disjAxiom.getObjectPropertiesInSignature().iterator();
                while (var18.hasNext()) {
                    OWLObjectProperty oce1 = (OWLObjectProperty) var18.next();
                    this.printObjectProperty(oce1);
                    if(var18.hasNext())
                        System.out.print(" isn't a kind of ");
                }
            } else if (a instanceof OWLEquivalentClassesAxiom) {
                OWLEquivalentClassesAxiom ax = (OWLEquivalentClassesAxiom) a;
                List<OWLClassExpression> oceList = ax.getClassExpressionsAsList();
                OWLClassExpression oc1 = oceList.get(0);
                OWLClassExpression oc2 = oceList.get(1);
                OWLClass sub = oc1.asOWLClass();  // By default, the first class is atomic.
                this.printSubsumption(sub, oc2);
                
            } else if (a instanceof OWLEquivalentDataPropertiesAxiom) {
                OWLEquivalentDataPropertiesAxiom ax = (OWLEquivalentDataPropertiesAxiom) a;                
                int size=ax.getProperties().size();
                int counter=1;
                for(OWLDataPropertyExpression dpe : ax.getProperties()) {
                	this.printDataProperty(dpe);
                    if (counter < size) {
                        System.out.print(" is a kind of ");
                    }
                    counter ++;
                }
            } else if (a instanceof OWLEquivalentObjectPropertiesAxiom){
                OWLEquivalentObjectPropertiesAxiom EOPA= (OWLEquivalentObjectPropertiesAxiom)a;
                int size =  EOPA.getProperties().size();
                int counter = 1;
                for(OWLObjectPropertyExpression ope : EOPA.getProperties()) {
                	this.printObjectProperty(ope);
                    if (counter < size) {
                        System.out.print(" is a kind of ");
                    }
                	counter++;
                }
            } else if (a instanceof OWLSubObjectPropertyOfAxiom) {
            	OWLSubObjectPropertyOfAxiom ax = (OWLSubObjectPropertyOfAxiom) a;
            	OWLObjectPropertyExpression sub = ax.getSubProperty();
                this.printObjectProperty(sub);
                OWLObjectPropertyExpression ope = ax.getSuperProperty();
                if(!ope.isAnonymous()) {
                	System.out.print(" is a kind of ");
                } 
                this.printObjectProperty(sub, ope);
                
            } else if (a instanceof OWLSubDataPropertyOfAxiom) {
                OWLSubDataPropertyOfAxiom ax = (OWLSubDataPropertyOfAxiom) a;
                this.printDataProperty(ax.getSubProperty());
                System.out.print(" is a kind of ");
                this.printDataProperty(ax.getSuperProperty());
            } else if(a instanceof OWLSubPropertyChainOfAxiom) {
            	OWLSubPropertyChainOfAxiom ax = (OWLSubPropertyChainOfAxiom)a;
            	System.out.print("Chain of ");
            	int counter = 0;
            	for(OWLObjectPropertyExpression ope : ax.getPropertyChain()) {
            		this.printObjectProperty(ope);
            		counter ++;
            		if(counter < ax.getPropertyChain().size()) {
            			System.out.print(" and ");
            		}            		
            	}
            	System.out.print(" is a kind of ");
            	this.printObjectProperty(ax.getSuperProperty());
                
                // The followings consider the properties of OWLObjectProperty or OWLDataProperty
            } else if (a instanceof OWLInverseObjectPropertiesAxiom) {
                OWLInverseObjectPropertiesAxiom ax = (OWLInverseObjectPropertiesAxiom) a;
                this.printObjectProperty(ax.getFirstProperty());
                System.out.print(" is reverse to ");
                this.printObjectProperty(ax.getSecondProperty());
            } else if (a instanceof OWLFunctionalObjectPropertyAxiom) {
                OWLFunctionalObjectPropertyAxiom ax = (OWLFunctionalObjectPropertyAxiom) a;
                this.printObjectProperty(ax.getProperty());
                System.out.print(" is functional");
            } else if (a instanceof OWLFunctionalDataPropertyAxiom) {
                OWLFunctionalDataPropertyAxiom ax = (OWLFunctionalDataPropertyAxiom) a;
                this.printDataProperty(ax.getProperty());
                System.out.print(" is functional");
            } else if(a instanceof OWLTransitiveObjectPropertyAxiom){
                OWLTransitiveObjectPropertyAxiom ax=(OWLTransitiveObjectPropertyAxiom)a;
                this.printObjectProperty(ax.getProperty());
                System.out.print(" is Transitive");
            } else if(a instanceof OWLInverseFunctionalObjectPropertyAxiom){
                OWLInverseFunctionalObjectPropertyAxiom ax=(OWLInverseFunctionalObjectPropertyAxiom)a;
                this.printObjectProperty(ax.getProperty());
                System.out.print(" is Inversefunctional");
            } else if(a instanceof OWLSymmetricObjectPropertyAxiom){
                OWLSymmetricObjectPropertyAxiom ax=(OWLSymmetricObjectPropertyAxiom) a;
                this.printObjectProperty(ax.getProperty());
                System.out.print(" is Symmetric");
            } else if(a instanceof OWLIrreflexiveObjectPropertyAxiom) {
            	OWLIrreflexiveObjectPropertyAxiom ax=(OWLIrreflexiveObjectPropertyAxiom) a;
                this.printObjectProperty(ax.getProperty());
                System.out.print(" is Irreflexive"); 
            } else if(a instanceof OWLAsymmetricObjectPropertyAxiom) {
            	OWLAsymmetricObjectPropertyAxiom ax=(OWLAsymmetricObjectPropertyAxiom) a;
                this.printObjectProperty(ax.getProperty());
                System.out.print(" is Asymmetric"); //AsymmetricObjectProperty
            } else if(a instanceof OWLReflexiveObjectPropertyAxiom) {
            	OWLReflexiveObjectPropertyAxiom ax = (OWLReflexiveObjectPropertyAxiom)a;
            	 this.printObjectProperty(ax.getProperty());
                 System.out.print(" is Reflexive");
                
                // The followings consider assertion axioms
            } else if (a instanceof OWLClassAssertionAxiom) {
                OWLClassAssertionAxiom ax = (OWLClassAssertionAxiom) a;
                this.printIndividual(ax.getIndividual());
                System.out.print(" is a ");                
                printConcept(ax.getClassExpression());
            } else if (a instanceof OWLObjectPropertyAssertionAxiom) {
                OWLObjectPropertyAssertionAxiom ax = (OWLObjectPropertyAssertionAxiom) a;
                this.printIndividual(ax.getSubject());
                System.out.print(" ");
                this.printObjectProperty(ax.getProperty());
                System.out.print(" ");
                this.printIndividual(ax.getObject());                
            } else if (a instanceof OWLDataPropertyAssertionAxiom) {
                OWLDataPropertyAssertionAxiom ax = (OWLDataPropertyAssertionAxiom) a;           
                this.printIndividual(ax.getSubject());
                System.out.print(" ");
                this.printDataProperty(ax.getProperty());
                System.out.print(" "+ax.getObject().getLiteral().toString()); 
            } else if (a instanceof OWLDifferentIndividualsAxiom) {
            	OWLDifferentIndividualsAxiom ax = (OWLDifferentIndividualsAxiom) a;
            	for(OWLIndividual indi : ax.getIndividuals()) {
            		this.printIndividual(indi);
            		System.out.print(", ");
            	}
            	System.out.print(" are different.");
            } else {
            	System.err.println("\nCannot translate this axiom: "+a.toString());
            }
        }

        System.out.println();
    }
    
    public void printSubsumption(OWLClassExpression sub, OWLClassExpression supoce) {
    	if(sub.isAnonymous()) {
    		printConcept(sub);
    	} else {
    		System.out.print(OWLTools.getLocalName(sub.asOWLClass())+" ");   
    	}
    	      
        if(!supoce.isAnonymous()) {
        	System.out.print(" is a kind of ");
        	OWLClass oc = supoce.asOWLClass();
            System.out.print(OWLTools.getLocalName(oc));
        } else if (supoce instanceof OWLObjectUnionOf) {        	
            OWLObjectUnionOf upExp = (OWLObjectUnionOf) supoce;                
            printUnion(sub, upExp);
            //NatrualOWL
        } else if (supoce instanceof OWLObjectIntersectionOf) {
        	OWLObjectIntersectionOf upExp = (OWLObjectIntersectionOf) supoce;   
            printIntersection(sub, upExp);
        } else if (supoce instanceof OWLObjectComplementOf) {
        	System.out.print(" is not a kind of ");
            OWLObjectComplementOf upExp = (OWLObjectComplementOf) supoce;
            printConcept(upExp.getOperand());
            System.out.print(". ");
        } else if(supoce instanceof OWLObjectOneOf){
        	System.out.print(" is one of ");
            OWLObjectOneOf upExp=(OWLObjectOneOf) supoce;
            System.out.print(upExp.toString());
            System.out.print(". ");
        } else {
        	printConcept(supoce);
        }
    }
    
    public void printUnion(OWLClassExpression sub, OWLObjectUnionOf upExp) {
    	HashSet<OWLClass> atomicCl = new HashSet<OWLClass>();
        HashSet<OWLClassExpression> anonymousCl = new HashSet<OWLClassExpression>();
        for (OWLClassExpression oce1 : upExp.getOperands()) {
        	if(oce1.isAnonymous()) {
        		anonymousCl.add(oce1);
        	} else {
        		atomicCl.add(oce1.asOWLClass());
        	}
        }
        // output atomic classes
        if(atomicCl.size() > 0) {
        	System.out.print(" is a kind of ");
        	int counter = 0;
        	for(OWLClass oc : atomicCl) {
                System.out.print(OWLTools.getLocalName(oc));
                counter ++;
                if(counter < atomicCl.size()) {
                	System.out.print(" or ");
                }
            }
        	System.out.print(". ");
        	
        	// If both atomic classes and anyonymous classes exist, the sub-concept should be output twice.
        	if(anonymousCl.size()>0) { 
        		if(sub.isAnonymous()) {
            		printConcept(sub);
            	} else {
            		System.out.print(OWLTools.getLocalName(sub.asOWLClass())+" "); 
            	}
        	}
        }
        
        //output anonymous classes
        if(anonymousCl.size()>0) { 
        	int counter = 0;
        	for (OWLClassExpression oce1 : anonymousCl) {   
        		if((oce1.getObjectPropertiesInSignature().size()==0 && 
        				oce1.getDataPropertiesInSignature().size()==0) && counter == 0) {
        			System.out.print(" is a kind of ");
        		}
            	printConcept(oce1);
            	counter ++;
            	if(counter < anonymousCl.size()) {
                	System.out.print(" or ");
                }                       	                 	
            }
        	System.out.print(". ");   
        }
    }
    
    public void printIntersection(OWLClassExpression sub, OWLObjectIntersectionOf supoce) {    	
        HashSet<OWLClass> atomicCl = new HashSet<OWLClass>();
        HashSet<OWLClassExpression> anonymousCl = new HashSet<OWLClassExpression>();
        for (OWLClassExpression oce1 : supoce.getOperands()) {
        	if(oce1.isAnonymous()) {
        		anonymousCl.add(oce1);
        	} else {
        		atomicCl.add(oce1.asOWLClass());
        	}
        }
        // output atomic classes
        if(atomicCl.size() > 0) {
        	System.out.print(" is a kind of ");
        	int counter = 0;
        	for(OWLClass oc : atomicCl) {
                System.out.print(OWLTools.getLocalName(oc));
                counter ++;
                if(counter < atomicCl.size()) {
                	System.out.print(" and ");
                }
            }
        	System.out.print(". ");
        }
        
        //output anonymous classes
        if(anonymousCl.size()>0) {   
        	if(atomicCl.size() > 0) {
        		if(sub.isAnonymous()) {
        			printConcept(sub);
        		} else {
        			System.out.print(OWLTools.getLocalName(sub.asOWLClass())+" "); 
        		}        		
        	}
    		int counter = 0;
        	for (OWLClassExpression oce1 : anonymousCl) {      
        		if((oce1.getObjectPropertiesInSignature().size()==0 && 
        				oce1.getDataPropertiesInSignature().size()==0) && counter == 0) {
        			System.out.print(" is a kind of ");
        		}
            	printConcept(oce1);
            	counter ++;
            	if(counter < anonymousCl.size()) {
                	System.out.print(" and ");
                }                    	                	
            }
        	System.out.print(". ");    
        }
    }

    public void printConcept(OWLClassExpression oce) {

        if (!oce.isAnonymous()) {
        	OWLClass oc = oce.asOWLClass();
            System.out.print(OWLTools.getLocalName(oc));
            
        } else if (oce instanceof OWLObjectUnionOf) {
            OWLObjectUnionOf upExp = (OWLObjectUnionOf) oce;
            int size = upExp.getOperands().size();
            int counter = 1;
            for (Iterator var5 = upExp.getOperands().iterator(); var5.hasNext(); ++counter) {
            	OWLClassExpression  oce1 = (OWLClassExpression) var5.next();            	
                printConcept(oce1);
                if (counter < size) {
                    System.out.print(" or ");
                }
            }
            //NatrualOWL
        } else if (oce instanceof OWLObjectIntersectionOf) {
            OWLObjectIntersectionOf upExp = (OWLObjectIntersectionOf) oce;                        
            int size = upExp.getOperands().size();
            int counter = 1;
            for (Iterator var5 = upExp.getOperands().iterator(); var5.hasNext(); ++counter) {
            	OWLClassExpression oce1 = (OWLClassExpression) var5.next();
                printConcept(oce1);
                if (counter < size) {
                    System.out.print(" and ");
                }
            }
        } else if (oce instanceof OWLObjectComplementOf) {
            OWLObjectComplementOf upExp = (OWLObjectComplementOf) oce;
            System.out.print(" not (");
            printConcept(upExp.getOperand());
            System.out.print(")");
        } else if(oce instanceof OWLObjectOneOf){
            OWLObjectOneOf upExp=(OWLObjectOneOf) oce;
            System.out.print(" one of ");
            // We only consider the case that atomic entities are contained in such an axiom
            int counter = 0;
            for(OWLEntity ent : upExp.getSignature()) {
            	System.out.print(OWLTools.getLocalName(ent));
            	counter ++;
            	if(counter < upExp.getSignature().size()) {
            		System.out.print(" and ");
            	}
            }
        } 
        else {
            //NatrualOWL
            if (oce instanceof OWLObjectSomeValuesFrom) {
                OWLObjectSomeValuesFrom supExp = (OWLObjectSomeValuesFrom) oce;
                this.printObjectProperty(supExp.getProperty());
                System.out.print(" at least one ");
                printConcept((OWLClassExpression) supExp.getFiller());
                //NatrualOWL
            } else if (oce instanceof OWLObjectAllValuesFrom) {
                OWLObjectAllValuesFrom supExp = (OWLObjectAllValuesFrom) oce;
                this.printObjectProperty((OWLObjectPropertyExpression) supExp.getProperty());
                System.out.print(" only ");
                printConcept((OWLClassExpression) supExp.getFiller());
                //NatrualOWL
            } else if (oce instanceof OWLDataHasValue){
                OWLDataHasValue supExp=(OWLDataHasValue)oce;
                this.printDataProperty((OWLDataPropertyExpression)supExp.getProperty());
                System.out.print(" "+supExp.getValue().getLiteral());
                //NatrualOWL
            } else if(oce instanceof  OWLObjectHasValue) {
                OWLObjectHasValue supExp=(OWLObjectHasValue)oce;
                this.printObjectProperty((OWLObjectPropertyExpression)supExp.getProperty());
                System.out.print(" ");
                this.printIndividual(supExp.getValue());
            } else {

                if (oce instanceof OWLDataSomeValuesFrom) {
                    OWLDataSomeValuesFrom supExp = (OWLDataSomeValuesFrom) oce;
                    this.printDataProperty((OWLDataPropertyExpression) supExp.getProperty());
                    System.out.print(" at least one ");
                    System.out.print(((OWLDataRange) supExp.getFiller()).toString());
                } else if (oce instanceof OWLDataAllValuesFrom) {
                    OWLDataAllValuesFrom supExp = (OWLDataAllValuesFrom) oce;
                    this.printDataProperty((OWLDataPropertyExpression) supExp.getProperty());
                    System.out.print(" only ");
                    System.out.print(((OWLDataRange) supExp.getFiller()).toString());
                } else if (oce instanceof OWLObjectMinCardinality) {
                    OWLObjectMinCardinality car = (OWLObjectMinCardinality) oce;
                    this.printObjectProperty((OWLObjectPropertyExpression) car.getProperty());
                    System.out.print(" at least " + car.getCardinality() + " ");
                    printConcept((OWLClassExpression) car.getFiller());
                } else if (oce instanceof OWLObjectMaxCardinality) {
                    OWLObjectMaxCardinality car = (OWLObjectMaxCardinality) oce;
                    this.printObjectProperty((OWLObjectPropertyExpression) car.getProperty());
                    System.out.print(" at most " + car.getCardinality() + " ");
                    printConcept((OWLClassExpression) car.getFiller());
                } else if (oce instanceof OWLObjectExactCardinality) {
                    OWLObjectExactCardinality car = (OWLObjectExactCardinality) oce;
                    this.printObjectProperty(((OWLObjectPropertyExpression) car.getProperty()));
                    System.out.print(" exactly "+ car.getCardinality()+" ");
                    printConcept((OWLClassExpression) car.getFiller());
                } else if (oce instanceof OWLDataMinCardinality) {
                    OWLDataMinCardinality car = (OWLDataMinCardinality) oce;
                    this.printDataProperty((OWLDataPropertyExpression) car.getProperty());
                    System.out.print(" at least " + car.getCardinality() + " ");                    
                    System.out.print(((OWLDataRange) car.getFiller()).asOWLDatatype().toString());
                } else if (oce instanceof OWLDataMaxCardinality) {
                    OWLDataMaxCardinality car = (OWLDataMaxCardinality) oce;
                    this.printDataProperty((OWLDataPropertyExpression) car.getProperty());
                    System.out.print(" at most " + car.getCardinality() + " ");                    
                    System.out.print(((OWLDataRange) car.getFiller()).toString());                
                } else if (oce instanceof OWLDataExactCardinality) {
                    OWLDataExactCardinality car = (OWLDataExactCardinality) oce;                    
                    this.printDataProperty((OWLDataPropertyExpression) car.getProperty());
                    System.out.print(" exactly " + car.getCardinality() + " ");
                    System.out.print(((OWLDataRange) car.getFiller()).toString());
                } 
            }

        }
    }
    
    private void printObjectProperty(OWLObjectPropertyExpression subp, OWLObjectPropertyExpression ope) {
    	if(!ope.isAnonymous()) {
    		OWLObjectProperty op = ope.asOWLObjectProperty();
    		System.out.print(OWLTools.getLocalName(op));
    	} else {
    		if(ope instanceof OWLObjectInverseOf) {
    			OWLObjectInverseOf op2 = (OWLObjectInverseOf)ope;
    			System.out.print(" is inverse of ");
    			OWLObjectPropertyExpression iop = op2.getInverse();
    			this.printObjectProperty(iop);    			
    		} else {
    			System.err.println("complex op: "+ope.toString());
    		}    		
    	}
    }
    
    private void printObjectProperty(OWLObjectPropertyExpression ope) {
    	if(!ope.isAnonymous()) {
    		OWLObjectProperty op = ope.asOWLObjectProperty();
    		System.out.print(OWLTools.getLocalName(op));
    	} else {
    		if(ope instanceof OWLObjectInverseOf) {
    			OWLObjectInverseOf op2 = (OWLObjectInverseOf)ope;
    			System.out.print(" inverse of ");
    			OWLObjectPropertyExpression iop = op2.getInverse();
    			this.printObjectProperty(iop);    			
    		} else {
    			System.err.println("complex op: "+ope.toString());
    		}    		
    	}
    }
    
    private void printDataProperty(OWLDataPropertyExpression ope) {
    	if(!ope.isAnonymous()) {
    		OWLDataProperty op = ope.asOWLDataProperty();
    		System.out.print(OWLTools.getLocalName(op));
    		
    	} else {
    		System.err.println("complex dp: "+ope.toString());
    	}
    }
    
    private void printIndividual(OWLIndividual indi) {
    	if(indi.isNamed()) {
    		System.out.print(OWLTools.getLocalName(indi.asOWLNamedIndividual()));
    	}  else {
    		System.err.println("complex indi: "+indi.toString());
    	}
    	
    }
}
