package uk.gov.companieshouse.servicesdashboardapi.utils;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.servicesdashboardapi.model.github.GitReleaseInfo;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GitUtilsTest {

    @Test
    void shouldReturnEmptyListWhenGitReleasesIsEmpty() {
        List<GitReleaseInfo> releases = new ArrayList<>();

        List<GitReleaseInfo> result = GitUtils.filterReleases(releases);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldHandleAlphabeticReleasePatternWhenFiltering() {
        GitReleaseInfo first = new GitReleaseInfo();
        first.setVersion("abc");
        first.setDate("2026-01-23");

        GitReleaseInfo matchingAlphabetic = new GitReleaseInfo();
        matchingAlphabetic.setVersion("xyz");
        matchingAlphabetic.setDate("2026-01-22");

        GitReleaseInfo differentPattern = new GitReleaseInfo();
        differentPattern.setVersion("123");
        differentPattern.setDate("2026-01-21");

        List<GitReleaseInfo> releases = new ArrayList<>();
        releases.add(first);
        releases.add(matchingAlphabetic);
        releases.add(differentPattern);

        List<GitReleaseInfo> result = GitUtils.filterReleases(releases);

        assertEquals(2, result.size());
        assertSame(first, result.get(0));
        assertSame(differentPattern, result.get(1));
    }

    @Test
    void shouldOnlyAddOneIfAllMatchingVersions() {
        GitReleaseInfo first = new GitReleaseInfo();
        first.setVersion("1.2.3");
        first.setDate("2026-01-23");

        GitReleaseInfo matching = new GitReleaseInfo();
        matching.setVersion("1.2.3");
        matching.setDate("2026-01-22");

        List<GitReleaseInfo> releases = new ArrayList<>();
        releases.add(first);
        releases.add(matching);

        List<GitReleaseInfo> result = GitUtils.filterReleases(releases);

        assertEquals(1, result.size());
        assertSame(first, result.get(0));
    }

    @Test
    void shouldAddFirstNonMatchingReleaseToFilteredList() {
        GitReleaseInfo first = new GitReleaseInfo();
        first.setVersion("1.2.3");
        first.setDate("2026-01-23");

        GitReleaseInfo nonMatching = new GitReleaseInfo();
        nonMatching.setVersion("release-1.2.3");
        nonMatching.setDate("2026-01-22");

        List<GitReleaseInfo> releases = new ArrayList<>();
        releases.add(first);
        releases.add(nonMatching);

        List<GitReleaseInfo> result = GitUtils.filterReleases(releases);

        assertEquals(2, result.size());
        assertSame(first, result.get(0));
        assertSame(nonMatching, result.get(1));
    }

    @Test
    void shouldKeepFirstReleaseAndFirstDifferentReleaseCycle() {
        GitReleaseInfo first = new GitReleaseInfo();
        first.setVersion("ecs-service-1.0.22");
        first.setDate("2026-01-22");

        GitReleaseInfo sameCycleOne = new GitReleaseInfo();
        sameCycleOne.setVersion("ecs-service-1.0.21");
        sameCycleOne.setDate("2026-01-21");

        GitReleaseInfo sameCycleTwo = new GitReleaseInfo();
        sameCycleTwo.setVersion("ecs-service-1.0.20");
        sameCycleTwo.setDate("2026-01-20");

        GitReleaseInfo differentCycle = new GitReleaseInfo();
        differentCycle.setVersion("4.0.11");
        differentCycle.setDate("2025-12-11");

        List<GitReleaseInfo> releases = new ArrayList<>();
        releases.add(first);
        releases.add(sameCycleOne);
        releases.add(sameCycleTwo);
        releases.add(differentCycle);

        List<GitReleaseInfo> result = GitUtils.filterReleases(releases);

        assertEquals(2, result.size());
        assertEquals("ecs-service-1.0.22", result.get(0).getVersion());
        assertEquals("4.0.11", result.get(1).getVersion());
    }
}
