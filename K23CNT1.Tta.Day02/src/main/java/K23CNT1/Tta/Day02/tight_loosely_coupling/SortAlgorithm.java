package K23CNT1.Tta.Day02.tight_loosely_coupling;
import java.util.Arrays;

interface SortAlgorithm {
    void sort(int[] array);
}

class LooselyBubbleSortAlgorithm implements SortAlgorithm {
    @Override
    public void sort(int[] array) {
        System.out.println("Sorted using bubble sort algorithm");
        Arrays.stream(array).sorted().forEach(System.out::println);
    }
}

