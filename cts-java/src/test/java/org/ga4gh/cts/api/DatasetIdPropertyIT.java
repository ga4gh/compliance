package org.ga4gh.cts.api;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests to make sure we can override the ID of the compliance test dataset.
 *
 * @author Herb Jellinek
 */
public class DatasetIdPropertyIT {

    private static final String PROP_NAME = "ctk.tgt.dataset_id";

    /**
     * Check that {@link TestData#getDatasetId()} returns the default dataset ID when
     * there's no overriding value in the Java {@link System} properties.
     */
    @Test
    public void checkDefaultValue() {
        final String originalValue = System.getProperty(PROP_NAME);
        try {
            System.clearProperty(PROP_NAME);
            assertThat(System.getProperty(PROP_NAME)).isNull();

            // the real test:
            assertThat(TestData.getDatasetId()).isEqualTo(TestData.DEFAULT_DATASET_ID);

        } finally {
            // the system properties are a global resource, so clean up
            if (originalValue == null) {
                System.clearProperty(PROP_NAME);
            } else {
                System.setProperty(PROP_NAME, originalValue);
            }
        }
    }

    /**
     * Check that {@link TestData#getDatasetId()} returns the override dataset ID when
     * there's an overriding value in the Java {@link System} properties.
     */
    @Test
    public void checkOverrideValue() {
        final String originalValue = System.getProperty(PROP_NAME);
        try {
            final String madeUpId = Utils.randomId();

            System.setProperty(PROP_NAME, madeUpId);
            assertThat(System.getProperty(PROP_NAME)).isEqualTo(madeUpId);

            // the real test:
            assertThat(TestData.getDatasetId()).isEqualTo(madeUpId);

        } finally {
            // the system properties are a global resource, so clean up
            if (originalValue == null) {
                System.clearProperty(PROP_NAME);
            } else {
                System.setProperty(PROP_NAME, originalValue);
            }
        }
    }

}
