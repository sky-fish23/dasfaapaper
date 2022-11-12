package com.company;
import java.util.Iterator;
import org.semanticweb.owlapi.model.*;

public class AXintoNL {

    public static void main(String[] args) throws Exception {
        String ontoRoot = "data2/";
        String ontoName = "University.owl";
        OWLOntology o = OntoUtils.openOntology("file:" + ontoRoot + ontoName);
        String str = "<http://omv.ontoware.org/2005/05/ontology#";
        System.out.println(o.getLogicalAxiomCount()); // Obtain the number of axioms
        for(OWLAxiom ax: o.getLogicalAxioms()) {
            ax=ax.getAxiomWithoutAnnotations();
            String axStr = ax.toString();
            System.out.println("**** " + axStr);
            printAxiomInfo(ax, str);
        }

    }

    public static void printAxiomInfo(OWLAxiom a, String str) {
        //NatrualOWL
        if (a instanceof OWLSubClassOfAxiom) {
            OWLSubClassOfAxiom subAxiom = (OWLSubClassOfAxiom) a;
            OWLClassExpression oce1 = subAxiom.getSubClass();
            OWLClassExpression range = subAxiom.getSuperClass();
            printConcept(oce1, str);
            System.out.print(" is a kind of ");
            printConcept(range, str);
        } else {
            //Our own transformation rules
            if (a instanceof OWLObjectPropertyDomainAxiom) {
                OWLObjectPropertyDomainAxiom domainAxiom = (OWLObjectPropertyDomainAxiom) a;
                OWLObjectPropertyExpression opInRangeAxiom = (OWLObjectPropertyExpression) domainAxiom.getProperty();
                OWLClassExpression range = domainAxiom.getDomain();
                System.out.print(opInRangeAxiom.toString() + " has domain in ");
                printConcept(range, str);
                //Our own transformation rules
            } else if (a instanceof OWLDataPropertyDomainAxiom) {
                OWLDataPropertyDomainAxiom domainAxiom = (OWLDataPropertyDomainAxiom) a;
                OWLDataPropertyExpression opInDomainAxiom = (OWLDataPropertyExpression) domainAxiom.getProperty();
                OWLClassExpression range = domainAxiom.getDomain();
                System.out.print(opInDomainAxiom.toString() + " has domain in ");
                printConcept(range, str);
                //Our own transformation rules
            } else if (a instanceof OWLObjectPropertyRangeAxiom) {
                OWLObjectPropertyRangeAxiom rangeAxiom = (OWLObjectPropertyRangeAxiom) a;
                OWLObjectPropertyExpression  opInRangeAxiom = (OWLObjectPropertyExpression) rangeAxiom.getProperty();
                OWLClassExpression range = (OWLClassExpression) rangeAxiom.getRange();
                System.out.print(opInRangeAxiom.toString() + " range in ");
                printConcept(range, str);
                //Our own transformation rules
            } else if (a instanceof OWLDataPropertyRangeAxiom) {
                OWLDataPropertyRangeAxiom rangeAxiom = (OWLDataPropertyRangeAxiom) a;
                System.out.print(((OWLDataPropertyExpression) rangeAxiom.getProperty()).toString() + " range in " + ((OWLDataRange) rangeAxiom.getRange()).toString());
            } //NatrualOWL
            else if (a instanceof OWLDisjointClassesAxiom) {
                OWLDisjointClassesAxiom disjAxiom = (OWLDisjointClassesAxiom) a;
                //System.out.print(" Disjoint classes: (");
                Iterator var18 = disjAxiom.getClassExpressions().iterator();

                while (var18.hasNext()) {
                    OWLClassExpression oce1 = (OWLClassExpression) var18.next();
                    printConcept(oce1, str);
                    if(var18.hasNext())
                        System.out.print("isn't a kind of ");
                }

                // System.out.print(")");
            } //Our own transformation rules
            else if (a instanceof OWLEquivalentClassesAxiom) {
                OWLEquivalentClassesAxiom ax = (OWLEquivalentClassesAxiom) a;
                int size = ax.getClassExpressions().size();
                int counter = 1;
                for (Iterator var5 = ax.getClassExpressions().iterator(); var5.hasNext(); ++counter) {
                    OWLClassExpression oce = (OWLClassExpression) var5.next();
                    printConcept(oce, str);
                    if (counter < size) {
                        System.out.print(" is equivalent to ");
                    }
                }
            }//Our own transformation rules
            else if (a instanceof OWLEquivalentDataPropertiesAxiom) {
                OWLEquivalentDataPropertiesAxiom ax = (OWLEquivalentDataPropertiesAxiom) a;
                int size=ax.getProperties().size();
                int counter=1;
                for (Iterator var5=ax.getProperties().iterator(); var5.hasNext();++counter) {
                    System.out.print(var5.next());
                    if (counter < size) {
                        System.out.print(" is equivalent to ");
                    }
                }
            }//Our own transformation rules
            else if (a instanceof OWLEquivalentObjectPropertiesAxiom){
                OWLEquivalentObjectPropertiesAxiom EOPA= (OWLEquivalentObjectPropertiesAxiom)a;
                int size =  EOPA.getProperties().size();
                int counter = 1;
                for (Iterator var5 =  EOPA.getProperties().iterator(); var5.hasNext(); ++counter) {
                    System.out.print(var5.next());
                    if (counter < size) {
                        System.out.print(" is equivalent to ");
                    }
                }
            }
            //Our own transformation rules
            else if (a instanceof OWLSubPropertyAxiom) {
                OWLSubPropertyAxiom ax = (OWLSubPropertyAxiom) a;
                OWLPropertyExpression subPr = ax.getSubProperty();
                OWLPropertyExpression supPr = ax.getSuperProperty();
                System.out.print(subPr.toString() + " is subproperty of " + supPr.toString());
            }
            //Our own transformation rules
            else if (a instanceof OWLClassAssertionAxiom) {
                OWLClassAssertionAxiom ax = (OWLClassAssertionAxiom) a;
                System.out.print(ax.getIndividual().toString() + " belongs to ");
                printConcept(ax.getClassExpression(), str);
            }
            else if (a instanceof OWLObjectPropertyAssertionAxiom) {
                OWLObjectPropertyAssertionAxiom ax = (OWLObjectPropertyAssertionAxiom) a;
                System.out.print(ax.getObject().toString() + " belongs to ");
                System.out.print(ax.getProperty().toString());
            }else if (a instanceof OWLDataPropertyAssertionAxiom) {
                OWLDataPropertyAssertionAxiom ax = (OWLDataPropertyAssertionAxiom) a;
                System.out.print(ax.getObject().toString() + " belongs to ");
                System.out.print(ax.getProperty().toString());
            }else if (a instanceof OWLInverseObjectPropertiesAxiom) {
                OWLInverseObjectPropertiesAxiom ax = (OWLInverseObjectPropertiesAxiom) a;
                System.out.print(ax.getFirstProperty().toString() + " is reverse to ");
                System.out.print(ax.getSecondProperty().toString());
            }else if (a instanceof OWLFunctionalObjectPropertyAxiom) {
                OWLFunctionalObjectPropertyAxiom ax = (OWLFunctionalObjectPropertyAxiom) a;
                System.out.print(ax.getProperty().toString() + " is ");
                System.out.print(" a functional object property");
            }else if (a instanceof OWLFunctionalDataPropertyAxiom) {
                OWLFunctionalDataPropertyAxiom ax = (OWLFunctionalDataPropertyAxiom) a;
                System.out.print(ax.getProperty().toString() + " is ");
                System.out.print(" a functional data property");
            }
            else if(a instanceof OWLTransitiveObjectPropertyAxiom){
                OWLTransitiveObjectPropertyAxiom ax=(OWLTransitiveObjectPropertyAxiom)a;
                System.out.print(ax.getProperty().toString()+"is");
                System.out.print(" Transitive");
            }
            else if(a instanceof OWLInverseFunctionalObjectPropertyAxiom){
                OWLInverseFunctionalObjectPropertyAxiom ax=(OWLInverseFunctionalObjectPropertyAxiom)a;
                System.out.print(ax.getProperty().toString()+"is");
                System.out.print(" Inversefunctional");
            }
            else if(a instanceof OWLSymmetricObjectPropertyAxiom){
                OWLSymmetricObjectPropertyAxiom ax=(OWLSymmetricObjectPropertyAxiom) a;
                System.out.print(ax.getProperty().toString()+"is");
                System.out.print(" Symmetric");
            }

        }

        System.out.println();
    }

    public static void printConcept(OWLClassExpression oce, String str) {

        if (!oce.isAnonymous()) {
//            System.out.print(oce.toString().replace(str,"").replace(">",""));
            System.out.print(oce.toString());
        } else {
            //NatrualOWL
            if (oce instanceof OWLObjectSomeValuesFrom) {
                OWLObjectSomeValuesFrom supExp = (OWLObjectSomeValuesFrom) oce;
                System.out.print(((OWLObjectPropertyExpression) supExp.getProperty()).toString() + "at least one ");
                printConcept((OWLClassExpression) supExp.getFiller(), str);
                //System.out.print(")");
                //NatrualOWL
            } else if (oce instanceof OWLObjectAllValuesFrom) {
                OWLObjectAllValuesFrom supExp = (OWLObjectAllValuesFrom) oce;
                System.out.print(((OWLObjectPropertyExpression) supExp.getProperty()).toString() + "only ");
                printConcept((OWLClassExpression) supExp.getFiller(), str);
                //NatrualOWL
            } else if (oce instanceof OWLDataHasValue){
                OWLDataHasValue supExp=(OWLDataHasValue)oce;
                System.out.print(((OWLDataPropertyExpression)supExp.getProperty()).toString()+supExp.getValue());
            }
            //NatrualOWL
            else if(oce instanceof  OWLObjectHasValue) {
                OWLObjectHasValue supExp=(OWLObjectHasValue)oce;
                System.out.print(((OWLObjectPropertyExpression)supExp.getProperty()).toString()+supExp.getValue());
            }else {
                //我们自己的转化规则转化的
                if (oce instanceof OWLDataSomeValuesFrom) {
                    OWLDataSomeValuesFrom supExp = (OWLDataSomeValuesFrom) oce;
                    System.out.print(" forall " + ((OWLDataPropertyExpression) supExp.getProperty()).toString() + "." + ((OWLDataRange) supExp.getFiller()).toString());
                } else if (oce instanceof OWLDataAllValuesFrom) {
                    OWLDataAllValuesFrom supExp = (OWLDataAllValuesFrom) oce;
                    System.out.print(" forall " + ((OWLDataPropertyExpression) supExp.getProperty()).toString() + "." + ((OWLDataRange) supExp.getFiller()).toString());
                } else if (oce instanceof OWLObjectMinCardinality) {
                    OWLObjectMinCardinality car = (OWLObjectMinCardinality) oce;
                    System.out.print(" >= " + car.getCardinality() + " " + ((OWLObjectPropertyExpression) car.getProperty()).toString() + ".(");
                    printConcept((OWLClassExpression) car.getFiller(), str);
                    System.out.print(")");
                } else if (oce instanceof OWLObjectMaxCardinality) {
                    OWLObjectMaxCardinality car = (OWLObjectMaxCardinality) oce;
                    System.out.print(" <= " + car.getCardinality() + " " + ((OWLObjectPropertyExpression) car.getProperty()).toString() + ".(");
                    printConcept((OWLClassExpression) car.getFiller(), str);
                    System.out.print(")");
                } else if (oce instanceof OWLObjectExactCardinality) {
                    OWLObjectExactCardinality car = (OWLObjectExactCardinality) oce;
                    System.out.print( ((OWLObjectPropertyExpression) car.getProperty()).toString() + "exactly "+ car.getCardinality()+" ");
                    printConcept((OWLClassExpression) car.getFiller(), str);
                } else if (oce instanceof OWLDataMinCardinality) {
                    OWLDataMinCardinality car = (OWLDataMinCardinality) oce;
                    System.out.print(" >=" + car.getCardinality() + " " + ((OWLDataPropertyExpression) car.getProperty()).toString() + "." + ((OWLDataRange) car.getFiller()).asOWLDatatype().toString());
                } else if (oce instanceof OWLDataMaxCardinality) {
                    OWLDataMaxCardinality car = (OWLDataMaxCardinality) oce;
                    System.out.print(" <=" + car.getCardinality() + " " + ((OWLDataPropertyExpression) car.getProperty()).toString() + "." + ((OWLDataRange) car.getFiller()).toString());
                } else if (oce instanceof OWLDataExactCardinality) {
                    OWLDataExactCardinality car = (OWLDataExactCardinality) oce;
                    System.out.print(" =" + car.getCardinality() + " " + ((OWLDataPropertyExpression) car.getProperty()).toString() + "." + ((OWLDataRange) car.getFiller()).toString());
                } else {
                    int size;
                    int counter;
                    OWLClassExpression oce1;
                    Iterator var5;
                    //Our own transformation rules
                    if (oce instanceof OWLObjectUnionOf) {
                        OWLObjectUnionOf upExp = (OWLObjectUnionOf) oce;
                        System.out.print("(");
                        size = upExp.getOperands().size();
                        counter = 1;

                        for (var5 = upExp.getOperands().iterator(); var5.hasNext(); ++counter) {
                            oce1 = (OWLClassExpression) var5.next();
                            printConcept(oce1, str);
                            if (counter < size) {
                                System.out.print(" or ");
                            }
                        }

                        System.out.print(")");
                        //NatrualOWL
                    } else if (oce instanceof OWLObjectIntersectionOf) {
                        OWLObjectIntersectionOf upExp = (OWLObjectIntersectionOf) oce;
                        System.out.print("(");
                        size = upExp.getOperands().size();
                        counter = 1;

                        for (var5 = upExp.getOperands().iterator(); var5.hasNext(); ++counter) {
                            oce1 = (OWLClassExpression) var5.next();
                            printConcept(oce1, str);
                            if (counter < size) {
                                System.out.print(" and ");
                            }
                        }
                        System.out.print(")");
                        //Our own transformation rules
                    } else if (oce instanceof OWLObjectComplementOf) {
                        OWLObjectComplementOf upExp = (OWLObjectComplementOf) oce;
                        System.out.print(" not (");
                        printConcept(upExp.getOperand(), str);
                        System.out.print(")");
                    }
                    else if(oce instanceof OWLObjectOneOf){
                        OWLObjectOneOf upExp=(OWLObjectOneOf) oce;
                        System.out.print("(is one of ");
                        System.out.print(upExp.toString());
                        System.out.print(")");
                    }


                }
            }

        }
    }
}
