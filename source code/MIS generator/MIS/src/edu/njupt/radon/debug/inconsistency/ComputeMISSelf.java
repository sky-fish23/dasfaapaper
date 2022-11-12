
package edu.njupt.radon.debug.inconsistency;

import java.util.HashSet;
import java.util.Vector;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;


public class ComputeMISSelf{

	static int number = 1;
	
	/** All the axioms in TBox to be processed */
	HashSet<OWLAxiom> allAxioms = new HashSet<OWLAxiom>();
	
			
	private HashSet<HashSet<OWLAxiom>> allMIS;
	private HashSet<Vector<OWLAxiom>> hittingSets;
	
	public static void main(String[] args) throws Exception{
		String ontoPath = "file:data/UOBM-lite-10-35.owl";
		OWLOntology o = OWL.manager.loadOntology(IRI.create(ontoPath));
		ComputeMISSelf main = new ComputeMISSelf();
		HashSet<HashSet<OWLAxiom>> allMIS = main.getAllMIS(o);
		// output results
		/*for(HashSet<OWLAxiom> mis : allMIS) {
			for(OWLAxiom ax : mis) {
				System.out.println("   "+ax.toString());
			}
			System.out.println();
		}*/
	}
	
		
	public ComputeMISSelf(){
		allMIS = new HashSet<HashSet<OWLAxiom>>();
		hittingSets = new HashSet<Vector<OWLAxiom>>();
	}
			
	public HashSet<OWLAxiom> getProcessedAxioms(){
		return new HashSet<OWLAxiom>(allAxioms);
	}
	
	public HashSet<HashSet<OWLAxiom>> getAllMIS(OWLOntology o) {
		try {			
			allAxioms = new HashSet<OWLAxiom>(o.getLogicalAxioms());			
			if (isConsistent(o)) {
				System.out.println("This ontology is consistent.");
				return null;
			}
			System.out.println("This ontology is inconsistent.");
			
            HashSet<OWLAxiom> allAxioms_t = new HashSet<OWLAxiom>(allAxioms);
			HashSet<HashSet<OWLAxiom>> allJusts = this.computeAllMIS(allAxioms_t);
						
			return allJusts;
		} catch (Exception e) {
			return null;
		}
	}
	

	public HashSet<HashSet<OWLAxiom>> getAllMIS(HashSet<OWLAxiom> ax) {
		allAxioms = new HashSet<OWLAxiom>(ax);			
		if (this.isConsistent(allAxioms)) {
			return new HashSet<HashSet<OWLAxiom>>();
		}
       
		this.computeAllMIS(allAxioms);
        			
		return new HashSet<HashSet<OWLAxiom>>(allMIS);	
	}
		
	public HashSet<HashSet<OWLAxiom>> computeAllMIS(HashSet<OWLAxiom> debuggedAxioms) {
		
		HashSet<OWLAxiom> debuggedAxioms_copy = new HashSet<OWLAxiom>(debuggedAxioms);
		HashSet<OWLAxiom> singleJust = null;
		allMIS.clear();
		hittingSets.clear();
			
		if(singleJust==null){
			if(!this.isConsistent(debuggedAxioms_copy)){				
				singleJust = singleJustBlackBox( debuggedAxioms_copy);		
			} else {
				return null;
			}
		}
        		   
		if (singleJust != null) {
			allMIS.add(singleJust);
			for (OWLAxiom a : singleJust) {
				Vector<OWLAxiom> path = new Vector<OWLAxiom>();
				debuggedAxioms_copy.remove(a);
				searchHST(debuggedAxioms_copy, a, path);
				debuggedAxioms_copy.add(a);
			}
		}

		return allMIS;

	}
	
	/**
	 * Mimic the algorithm to find all Justs in Bijan's ISWC'07 paper
	 */
	private void searchHST(HashSet<OWLAxiom> Oin, OWLAxiom a,
			Vector<OWLAxiom> path) {
		
		HashSet<OWLAxiom> O = (HashSet<OWLAxiom>)Oin.clone();		
		Vector<OWLAxiom> newPath = (Vector<OWLAxiom>) path.clone();
		HashSet<OWLAxiom> newJust = null;	
		
		newPath.add(a);
		//Early path termination
		for (Vector<OWLAxiom> h : hittingSets) {			
			if (newPath.containsAll(h)) { 
				return;
			}
		}
				
		//Justification reuse
		HashSet<OWLAxiom> O2 = (HashSet<OWLAxiom>)Oin.clone();
		O2.removeAll(newPath);			
		for (HashSet<OWLAxiom> exJust : allMIS ){
			if (O2.containsAll(exJust)){
				newJust = exJust;					
				break;
			}
		}
		
        //If no existing justification can be used, look for a new one
		if (newJust == null) { 
			if (!this.isConsistent(O)) {
				newJust = singleJustBlackBox(O);
			}						
		}		
			
		//If there is new justification found, then regard this one as a new root node for HSTree.
		if (newJust != null && newJust.size() > 0) {
			allMIS.add(newJust);
			for (OWLAxiom b : newJust) {
				O.remove(b);
				searchHST(O, b, newPath); 
				O.add(b);
			}
		} else {
			hittingSets.add(newPath);
		}		   
		
	}
	
	public HashSet<OWLAxiom> singleJustBlackBox(HashSet<OWLAxiom> allAxioms){
		
		HashSet<OWLAxiom> sos = new HashSet<OWLAxiom>();
		Vector<OWLAxiom> ax = new Vector<OWLAxiom>(allAxioms);
		
		//Fast pruning
		Vector<OWLAxiom> prunedAxioms = this.fastPrune(ax);
				
		//slow pruning
		HashSet<OWLAxiom> prunedAxioms_t = this.slowPrune(prunedAxioms);
		sos = new HashSet<OWLAxiom>(prunedAxioms_t);
		System.out.println("Found explanation <"+(number++)+"> : ");
		printOneSet(sos, null);
		return sos;
	}
	
	
	
	public static void printOneSet(
			HashSet<OWLAxiom> axioms,
			String ns){	
		
		Vector<OWLAxiom> set = new Vector<OWLAxiom>(axioms);
		int i = 1;
		if(set!=null){
			for(OWLAxiom a : set){
				if(ns!=null)
				    System.out.println("  ["+(i++)+"] "+a.toString().replace(ns, ""));
				else 
					System.out.println("  ["+(i++)+"] "+a.toString());		
			}
			System.out.println();
		}
	}

	public Vector<OWLAxiom> fastPrune(Vector<OWLAxiom> allRelated){
		//Fast pruning
		int pruneWindow = 10;
		Vector<OWLAxiom> prunedAxioms = new Vector<OWLAxiom>(allRelated);
		int size = allRelated.size();
		if(size>pruneWindow){
			int parts = size / pruneWindow;
			for (int part = 0; part < parts; part++) {
				for (int i = part * pruneWindow; i < part * pruneWindow
						+ pruneWindow; i++) {
					prunedAxioms.remove(allRelated.get(i));
				}
								
				if (this.isConsistent(new HashSet<OWLAxiom>(prunedAxioms))) {
					//System.out.println("Fast prune axioms");
					for (int i = part * pruneWindow; i < part * pruneWindow
							+ pruneWindow; i++) {
						prunedAxioms.add(allRelated.get(i));
						//System.out.println(i+" > "+allRelated.get(i).toString().replace(ns, ""));
					}
					
					//this.prune(part * pruneWindow, part * pruneWindow+ pruneWindow-1, allRelated, prunedAxioms, oc);
					
				}
			}
			if (size > parts * pruneWindow) {
				// add remaining from list to axioms
				for (int i = parts * pruneWindow; i < size; i++) {
					prunedAxioms.add(allRelated.get(i));
				}
			}
		}
		return prunedAxioms;
	}
	
	public HashSet<OWLAxiom> slowPrune(Vector<OWLAxiom> prunedAxioms){
		//System.out.println("Slow pruning : "+prunedAxioms.size());
		
		HashSet<OWLAxiom> prunedAxioms_c = new HashSet<OWLAxiom>(prunedAxioms);
		HashSet<OWLAxiom> prunedAxioms_t = new HashSet<OWLAxiom>(prunedAxioms);
		for(OWLAxiom a : prunedAxioms_c){			
			prunedAxioms_t.remove(a);
			
			if(this.isConsistent(prunedAxioms_t)){	
				prunedAxioms_t.add(a);
			}
		}
		return prunedAxioms_t;
	}
	
	public boolean isConsistent(
			OWLOntology o) {
		boolean flag = true;
		try {
			OWLReasonerFactory reasonerFactory = new PelletReasonerFactory();		
			OWLReasoner owlReasoner = reasonerFactory.createReasoner(o);
			flag = owlReasoner.isConsistent();			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return flag;
	}
	
	public boolean isConsistent(
			HashSet<OWLAxiom> ax) {
		boolean flag = true;
		try {
			OWLOntology o = OWL.manager.createOntology(ax);	
			flag = this.isConsistent(o);			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return flag;
	}
	
	public boolean isConsistent(
			Vector<OWLAxiom> ax) throws Exception {
		
		HashSet<OWLAxiom> axioms = new HashSet<OWLAxiom>(ax);
		
		return this.isConsistent(axioms);
	}
}
