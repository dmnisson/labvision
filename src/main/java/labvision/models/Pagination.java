package labvision.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Model for pagination of a list of entities
 * @author davidnisson
 *
 * @param <E> the entity being paginated
 */
public class Pagination<E> {
	/**
	 * Number of entities per page
	 */
	private int entitiesPerPage;
	
	/**
	 * List of entities being paginated
	 */
	private List<E> entityList;
	
	/**
	 * Number of pages
	 */
	private int numberOfPages;
	
	/**
	 * The maximum number of consecutive pages around the current page
	 * to which to display links
	 */
	private int numberOfConsecutivePagesToLink;
	
	/**
	 * Indices of first element of each page
	 */
	private HashMap<Integer, Integer> firstElementIndices;

	public int getEntitiesPerPage() {
		return entitiesPerPage;
	}

	public void setEntitiesPerPage(int entitiesPerPage) {
		this.entitiesPerPage = entitiesPerPage;
	}

	public List<E> getEntityList() {
		return entityList;
	}

	public void setEntityList(List<E> entityList) {
		this.entityList = entityList;
		computePagination();
	}

	/**
	 * Compute the number of pages and first element indices of each page
	 */
	public void computePagination() {
		// compute number of pages
		numberOfPages = entityList.size() / entitiesPerPage;
		if (entityList.size() % entitiesPerPage > 0) {
			numberOfPages++;
		}
		
		// repopulate first element index map
		firstElementIndices.clear();
		IntStream.range(1, numberOfPages + 1)
		.forEach(i -> {
			firstElementIndices.put(i, (i - 1) * entitiesPerPage);
		});
	}

	public int getNumberOfPages() {
		return numberOfPages;
	}

	/**
	 * Get the page numbers to which to link
	 * @param pageNumber the page number
	 * @return the page numbers
	 */
	public List<Integer> getPagesToLink(int pageNumber) {
		ArrayList<Integer> pageNumbersToLink = new ArrayList<>();
		
		// add first page
		pageNumbersToLink.add(1);
		
		// add range of pages around the current page
		int lowBound = Math.max(2, pageNumber - (numberOfConsecutivePagesToLink / 2));
		int highBound = Math.min(numberOfPages, 
				pageNumber + ceil2(numberOfConsecutivePagesToLink));
		pageNumbersToLink.addAll(IntStream.range(lowBound, highBound)
				.mapToObj(x -> x)
				.collect(Collectors.toList()));
		
		// add last page
		pageNumbersToLink.add(numberOfPages);
		
		return pageNumbersToLink;
	}
	
	// helper function
	// efficient way to compute ceil(x/2)
	private int ceil2(int x) {
		return (x / 2) + (x % 2);
	}
	
	/**
	 * Get the entities on a page
	 * @param pageNumber the page number
	 * @return the list of entities on the page
	 */
	public List<E> getPage(int pageNumber) {
		int firstElementIndex = firstElementIndices.get(pageNumber);
		return entityList.subList(firstElementIndex,
				Math.min(entityList.size(), firstElementIndex + entitiesPerPage));
	}

	/**
	 * Get the maximum number of consecutive pages around the current page
	 * to which to display links
	 * @return the maximum number of consecutive pages
	 */
	public int getNumberOfConsecutivePagesToLink() {
		return numberOfConsecutivePagesToLink;
	}

	/**
	 * Set the maximum number of consecutive pages around the current page
	 * to which to display links
	 * @return the maximum number of consecutive pages
	 */
	public void setNumberOfConsecutivePagesToLink(int numberOfConsecutivePagesToLink) {
		this.numberOfConsecutivePagesToLink = numberOfConsecutivePagesToLink;
	}
}
