package debug;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import com.clarkparsia.owlapiv3.OWL;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;


public class SatohJunkerDiagnosis extends SatohDiagnosis {

	private OWLAxiom[] axioms;
	private int numCheck;

	public SatohJunkerDiagnosis(OWLAxiom[] axioms, boolean[] fixed) {
		super(fixed);
		this.axioms = axioms;
		this.numCheck = 0;
	}

	/**
	 * @param args
	 * @throws OWLOntologyCreationException 
	 */
	public static void main(String[] args) throws OWLOntologyCreationException {
		if (args.length != 3) {
			System.out.println("Usage java SatohDiagnosis <-r|-f> <removable/fixed part> <ontology>");
			return;
		}

		long startTime = System.currentTimeMillis();
		OWLOntology ontology = OWL.manager.loadOntologyFromOntologyDocument(new File(args[2]));
		OWLAxiom[] axioms = new OWLAxiom[ontology.getAxiomCount()];
		axioms = ontology.getAxioms().toArray(axioms);
		OWL.manager.removeOntology(ontology);

		boolean removable = args[0].equalsIgnoreCase("-r"); 
		ontology = OWL.manager.loadOntologyFromOntologyDocument(new File(args[1]));
		boolean[] fixed = new boolean[axioms.length];
		for (int i = 0; i < axioms.length; i++)
			if (ontology.containsAxiom(axioms[i]))
				fixed[i] = !removable;
			else
				fixed[i] = removable;
		OWL.manager.removeOntology(ontology);
		System.out.printf("%%Loaded ontology in %dms%n", System.currentTimeMillis()-startTime);

		SatohJunkerDiagnosis diagnosis = new SatohJunkerDiagnosis(axioms, fixed);
		List<List<Integer>> diagnoses = diagnosis.execute();
		for (int i = 0; i < diagnoses.size(); i++) {
			System.out.printf("#%d diagnosis (%d axioms):%n", i+1, diagnoses.get(i).size());
			for (int num: diagnoses.get(i))
				System.out.println(axioms[num]);
		}
	}

	private List<Integer> computeRelaxation(boolean[] exist, List<Integer> relax) throws OWLOntologyCreationException {
		Set<OWLAxiom> as = new HashSet<OWLAxiom>();
		for (int j = 0; j < exist.length; j++)
			if (exist[j])
				as.add(axioms[j]);
		for (Integer num: relax)
			as.add(axioms[num.intValue()]);
		numCheck++;
		OWLOntology ontology = OWL.manager.createOntology(as);
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
		if (reasoner.isConsistent()) {
			reasoner.dispose();
			OWL.manager.removeOntology(ontology);
			return relax;
		}
		reasoner.dispose();
		OWL.manager.removeOntology(ontology);
		if (relax.size() == 1)
			return new ArrayList<Integer>();
		List<Integer> part1 = copy(relax, 0, relax.size()>>1); 
		List<Integer> relax1 = computeRelaxation(exist, part1);
		boolean[] exist1 = exist.clone();
		for (Integer num: relax1)
			exist1[num.intValue()] = true;
		List<Integer> part2 = copy(relax, relax.size()>>1, relax.size());
		List<Integer> relax2 = computeRelaxation(exist1, part2);
		return union(relax1, relax2);
	}

	private List<Integer> copy(List<Integer> part, int start, int end) {
		List<Integer> result = new ArrayList<Integer>();
		for (int i = start; i < end; i++)
			result.add(part.get(i));
		return result;
	}

	private List<Integer> union(List<Integer> part1, List<Integer> part2) {
		List<Integer> result = new ArrayList<Integer>();
		int i = 0, j = 0;
		while (i < part1.size() && j < part2.size()) {
			if (part1.get(i).intValue() < part2.get(j).intValue()) {
				result.add(part1.get(i));
				i++;
			}
			else if (part1.get(i).intValue() == part2.get(j).intValue()) {
				result.add(part1.get(i));
				i++;
				j++;
			}
			else {
				result.add(part2.get(j));
				j++;
			}
		}
		for (; i < part1.size(); i++)
			result.add(part1.get(i));
		for (; j < part2.size(); j++)
			result.add(part2.get(j));
		return result;
	}

	@Override
	public boolean handleDiagnosis(List<Integer> diag) {
		System.out.printf("%%Found diagnosis #%d (%d axioms, %d consistency checks) in %dms%n",
				diagnoses.size(), diag.size(), numCheck, getElapsedTime());
		return true;
	}

	@Override
	public boolean handleMinimalConflictSet(List<Integer> mcs) {
		System.out.printf("%%Found MCS #%d (%d axioms, %d consistency checks) in %dms%n",
			    numMCS, mcs.size(), numCheck, getElapsedTime());
		return true;
	}

	@Override
	public boolean findMaximalSuperset(boolean[] exist) {
		Set<OWLAxiom> as = new HashSet<OWLAxiom>();
		for (int j = 0; j < exist.length; j++)
			if (exist[j])
				as.add(axioms[j]);
		numCheck++;
		OWLOntology ontology;
		try {
			ontology = OWL.manager.createOntology(as);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
			return false;
		}
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
		if (!reasoner.isConsistent()) {
			reasoner.dispose();
			OWL.manager.removeOntology(ontology);
			return false;
		}
		reasoner.dispose();
		OWL.manager.removeOntology(ontology);
		List<Integer> relaxation = new ArrayList<Integer>();
		for (int j = 0; j < exist.length; j++)
			if (!exist[j])
				relaxation.add(j);
		if (relaxation.size() > 0) {
			try {
				relaxation = computeRelaxation(exist, relaxation);
			} catch (OWLOntologyCreationException e) {
				e.printStackTrace();
				return false;
			}
			for (int num: relaxation)
				exist[num] = true;
		}
		return true;
	}
}
