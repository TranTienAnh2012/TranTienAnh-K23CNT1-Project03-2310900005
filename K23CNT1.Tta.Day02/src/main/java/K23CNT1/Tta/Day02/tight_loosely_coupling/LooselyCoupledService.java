package K23CNT1.Tta.Day02.tight_loosely_coupling;

public class LooselyCoupledService {
    SortAlgorithm sortAlgorithm;

    public LooselyCoupledService(SortAlgorithm sortAlgorithm) {
        this.sortAlgorithm = sortAlgorithm;
    }

    void complexBusiness(int[] array) {
        sortAlgorithm.sort(array);
    }

    public static void main(String[] args) {
        LooselyCoupledService sortService =
                new LooselyCoupledService(new LooselyBubbleSortAlgorithm());
        sortService.complexBusiness(new int[]{13,21, 11, 42, 15});
    }
}