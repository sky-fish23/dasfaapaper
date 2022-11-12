package debug;

import java.util.ArrayList;
import java.util.List;

public abstract class SatohDiagnosis {

	protected boolean[] fixed;
	protected List<List<Integer>> diagnoses;
	protected int numMCS;
	protected long startTime;
	protected boolean finished;

	public SatohDiagnosis(boolean[] fx) {
		fixed = fx;
	}

	public List<List<Integer>> execute() {
		return execute(new ArrayList<List<Integer>>());
	}

	public List<List<Integer>> execute(List<List<Integer>> diag) {
		finished = false;
		startTime = System.currentTimeMillis();
		numMCS = 0;
		diagnoses = diag;
		constructDiagnoses(0, new ArrayList<Integer>(), null);
		return diagnoses;
	}

	public int getNumberOfMinimalConflictSets() {
		return numMCS;
	}

	public long getElapsedTime() {
		return System.currentTimeMillis()-startTime;
	}

	private void constructDiagnoses(int i, List<Integer> mhs, List<Integer>[] crit) {
		if (finished)
			return;
		if (i == diagnoses.size()) {
			boolean[] exist = fixed.clone();
			for (int num: mhs)
				exist[num] = true;
			if (findMaximalSuperset(exist)) {
				List<Integer> newDiag = new ArrayList<Integer>();
				for (int j = 0; j < exist.length; j++)
					if (!exist[j])
						newDiag.add(j);
				diagnoses.add(newDiag);
				finished = !handleDiagnosis(newDiag);
			}
			else {
				numMCS++;
				finished = !handleMinimalConflictSet(mhs);
				return;
			}
		}
		List<Integer> its = intersect(diagnoses.get(i), mhs);
		if (its.size() > 0) {
			@SuppressWarnings("unchecked")
			List<Integer>[] newCrit = new ArrayList[mhs.size()];
			for (int j = 0; j < mhs.size(); j++) {
				newCrit[j] = new ArrayList<Integer>();
				newCrit[j].addAll(crit[j]);
				if (its.size() == 1 && its.get(0) == mhs.get(j))
					newCrit[j].add(i);
			}
			constructDiagnoses(i+1, mhs, newCrit);
		}
		else {
			@SuppressWarnings("unchecked")
			List<Integer>[] newCrit = (crit == null)? new ArrayList[1]: new ArrayList[mhs.size()+1];
			for (int j = 0; j <= mhs.size(); j++)
				newCrit[j] = new ArrayList<Integer>();
			newCrit[mhs.size()].add(i);
			List<Integer> newMhs = new ArrayList<Integer>();
			for (int j = 0; j < diagnoses.get(i).size() && !finished; j++) {
				newMhs.clear();
				if (crit == null) {
					newMhs.add(diagnoses.get(i).get(j));
					constructDiagnoses(i+1, newMhs, newCrit);
				}
				else {
					int k = 0;
					for (; k < mhs.size(); k++) {
						newCrit[k].clear();
						for (Integer num: crit[k])
							if (diagnoses.get(num.intValue()).indexOf(diagnoses.get(i).get(j)) < 0)
								newCrit[k].add(num);
						if (newCrit[k].size() == 0)
							break;
					}
					if (k == mhs.size()) {
						newMhs.addAll(mhs);
						newMhs.add(diagnoses.get(i).get(j));
						constructDiagnoses(i+1, newMhs, newCrit);
					}
				}
			}
		}
	}

	private List<Integer> intersect(List<Integer> diag, List<Integer> mhs) {
		List<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < mhs.size(); i++)
			if (binarySearch(diag, mhs.get(i).intValue()))
				result.add(mhs.get(i));
		return result;
	}

	private boolean binarySearch(List<Integer> diag, int key) {
		int start = 0, end = diag.size();
		while (start < end) {
			int k = (start+end)>>1;
			if (diag.get(k).intValue() == key)
				return true;
			if (diag.get(k).intValue() < key)
				start = k+1;
			else
				end = k;
		}
		return false;
	}

	abstract public boolean handleDiagnosis(List<Integer> diag);
	abstract public boolean handleMinimalConflictSet(List<Integer> mcs);
	abstract public boolean findMaximalSuperset(boolean[] exist);

}
