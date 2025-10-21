package K23CNT1.Tta.Day02.tight_loosely_coupling;

import java.util.Arrays;

public class TightCouplingService {
    private BubbleSortAlgorithms bubbleSortAlgorithms = new BubbleSortAlgorithms();

    public TightCouplingService(BubbleSortAlgorithms bubbleSortAlgorithms) {
        this.bubbleSortAlgorithms = bubbleSortAlgorithms;
    }

    public void complexBusinessSort(int[] arr) {
        bubbleSortAlgorithms.sort(arr);
        Arrays.stream(arr).forEach(System.out::println);
    }

    public static void main(String[] args) {
        TightCouplingService service = new TightCouplingService(new BubbleSortAlgorithms());
        service.complexBusinessSort(new int[]{11, 21, 13, 42, 15});
    }
}
