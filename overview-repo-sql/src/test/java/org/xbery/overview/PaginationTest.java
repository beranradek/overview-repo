package org.xbery.overview;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Radek Beran
 */
public class PaginationTest {
    @Test
    public void numberOfCurrentPage() {
        assertEquals(1, new Pagination(0, 18, null).getPage());
        assertEquals(1, new Pagination(1, 18, null).getPage());
        assertEquals(2, new Pagination(18, 18, null).getPage());
        assertEquals(2, new Pagination(19, 18, null).getPage());
        assertEquals(3, new Pagination(36, 18, null).getPage());
        assertEquals(3, new Pagination(36, 18, Integer.valueOf(50)).getPage());
    }

    @Test
    public void pageCount() {
        assertEquals(null, new Pagination(0, 18, null).getPageCount());
        assertEquals(Integer.valueOf(1), new Pagination(1, 18, Integer.valueOf(16)).getPageCount());
        assertEquals(Integer.valueOf(1), new Pagination(18, 18, Integer.valueOf(18)).getPageCount());
        assertEquals(Integer.valueOf(2), new Pagination(19, 18, Integer.valueOf(19)).getPageCount());
        assertEquals(Integer.valueOf(2), new Pagination(36, 18, Integer.valueOf(36)).getPageCount());
        assertEquals(Integer.valueOf(3), new Pagination(36, 18, Integer.valueOf(37)).getPageCount());
    }
}
