package org.ga4gh.cts.api.rnaquantification;

import org.apache.avro.AvroRemoteException;
import org.ga4gh.ctk.CtkLogs;
import org.ga4gh.ctk.transport.URLMAPPING;
import org.ga4gh.ctk.transport.protocols.Client;
import org.ga4gh.cts.api.TestData;
import org.ga4gh.cts.api.Utils;
import org.ga4gh.methods.GAException;
import org.ga4gh.methods.SearchRnaQuantificationRequest;
import org.ga4gh.methods.SearchRnaQuantificationResponse;
import org.ga4gh.models.RnaQuantification;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test the <tt>/rnaquantification/search</tt> paging behavior.
 */
@Category(RnaQuantificationTests.class)
public class RnaQuantificationsPagingIT implements CtkLogs {

    private static Client client = new Client(URLMAPPING.getInstance());

    /**
     * Check that we can page 1 by 1 through the rnaquantifications we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.RnaQuantification#searchRnaQuantification(SearchRnaQuantificationRequest)}.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingOneByOneThroughRnaQuantifications() throws AvroRemoteException {
        final long start = 50;
        final long end = 100;

        final String rnaQuantificationId = Utils.getRnaQuantificationId(client);
        final List<RnaQuantification> listOfRnaQuantifications = Utils.getAllRnaQuantificationsInRange(client, rnaQuantificationId, start, end);

        // we will remove RnaQuantifications from this Set and assert at the end that we have zero
        final Set<RnaQuantification> setOfRnaQuantifications = new HashSet<>(listOfRnaQuantifications);
        assertThat(listOfRnaQuantifications).hasSize(setOfRnaQuantifications.size());

        // page through the rnaquantifications using the same query parameters
        String pageToken = null;
        for (RnaQuantification ignored : listOfRnaQuantifications) {
            final SearchRnaQuantificationRequest pageReq =
                    SearchRnaQuantificationRequest.newBuilder()
                                         .setRnaQuantificationId(rnaQuantificationSetId)
                                         .setReferenceName(TestData.REFERENCE_NAME)
                                         .setStart(start).setEnd(end)
                                         .setPageSize(1)
                                         .setPageToken(pageToken)
                                         .build();
            final SearchRnaQuantificationResponse pageResp = client.rnaQuantification.searchRnaQuantification(pageReq);
            final List<RnaQuantification> pageOfRnaQuantifications = pageResp.getRnaQuantifications();
            pageToken = pageResp.getNextPageToken();

            assertThat(pageOfRnaQuantifications).hasSize(1);
            assertThat(setOfRnaQuantifications).contains(pageOfRnaQuantifications.get(0));

            setOfRnaQuantifications.remove(pageOfRnaQuantifications.get(0));
        }

        assertThat(pageToken).isNull();
        assertThat(setOfRnaQuantifications).isEmpty();
    }

    /**
     * Check that we can page through the rnqQuantifications we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.RnaQuantification#searchRnaQuantification(SearchRnaQuantificationRequest)}
     * using an increment as large as the non-paged set of results.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingByOneChunkThroughRnaQuantifications() throws AvroRemoteException {
        final long start = 50;
        final long end = 100;

        final String rnaQuantificationId = Utils.getRnaQuantificationId(client);
        final List<RnaQuantification> listOfRnaQuantifications = Utils.getAllRnaQuantificationsInRange(client, rnaQuantificationId, start, end);

        // page through the rnaquantifications in one gulp
        checkSinglePageOfRnaQuantifications(rnaQuantificationId, start, end,
                                  listOfRnaQuantifications.size(),
                                  listOfRnaQuantifications);
    }

    /**
     * Check that we can page through the rnaquantifications we receive from
     * {@link org.ga4gh.ctk.transport.protocols.Client.RnaQuantification#searchRnaQuantification(SearchRnaQuantificationRequest)}
     * using an increment twice as large as the non-paged set of results.
     *
     * @throws AvroRemoteException if there's a communication problem or server exception ({@link GAException})
     */
    @Test
    public void checkPagingByOneTooLargeChunkThroughRnaQuantifications() throws AvroRemoteException {
        final long start = 50;
        final long end = 100;

        final String rnaQuantificationId = Utils.getRnaQuantificationId(client);
        final List<RnaQuantification> listOfRnaQuantifications = Utils.getAllRnaQuantificationsInRange(client, rnaQuantificationId, start, end);

        checkSinglePageOfRnaQuantifications(rnaQuantificationId, start, end,
                                  listOfRnaQuantifications.size() * 2,
                                  listOfRnaQuantifications);
    }

    /**
     * Check that we can receive expected results when we request a single
     * page of rnaQuantifications from {@link org.ga4gh.ctk.transport.protocols.Client.RnaQuantification#searchRnaQuantifications
     * (SearchRnaQuantificationRequest)}, using <tt>pageSize</tt> as the page size.
     *
     * @param rnaQuantificationId     the ID of the {@link RnaQuantification} we're paging through
     * @param start            the start value for the range we're searching
     * @param end              the end value for the range we're searching
     * @param pageSize         the page size we'll request
     * @param expectedRnaQuantifications all of the {@link RnaQuantification} objects we expect to receive
     * @throws AvroRemoteException if there's a communication problem or server exception
     */
    private void checkSinglePageOfRnaQuantifications(String rnaQuantificationId,
                                           long start, long end,
                                           int pageSize,
                                           List<RnaQuantification> expectedRnaQuantifications) throws AvroRemoteException {

        final SearchRnaQuantificationRequest pageReq =
                SearchRnaQuantificationRequest.newBuilder()
                                     .setRnaQuantificationId(rnaQuantificationId)
                                     .setReferenceName(TestData.REFERENCE_NAME)
                                     .setStart(start).setEnd(end)
                                     .setPageSize(pageSize)
                                     .build();
        final SearchRnaQuantificationResponse pageResp = client.rnaQuantification.searchRnaQuantification(pageReq);
        final List<RnaQuantification> pageOfRnaQuantifications = pageResp.getRnaQuantification();
        final String pageToken = pageResp.getNextPageToken();

        assertThat(pageOfRnaQuantifications).hasSize(expectedRnaQuantifications.size());
        assertThat(expectedRnaQuantifications).containsAll(pageOfRnaQuantifications);

        assertThat(pageToken).isNull();
    }

}
