package fr.free.nrw.commons.contributions

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.*
import fr.free.nrw.commons.Media
import fr.free.nrw.commons.auth.SessionManager
import fr.free.nrw.commons.media.MediaClient
import fr.free.nrw.commons.utils.NetworkUtilsTest
import fr.free.nrw.commons.utils.createMockDataSourceFactory
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import java.lang.RuntimeException
import java.util.*

/**
 * The unit test class for ContributionBoundaryCallbackTest
 */
class ContributionBoundaryCallbackTest {
    @Mock
    internal lateinit var repository: ContributionsRepository

    @Mock
    internal lateinit var sessionManager: SessionManager

    @Mock
    internal lateinit var mediaClient: MediaClient

    private lateinit var contributionBoundaryCallback: ContributionBoundaryCallback

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var scheduler: Scheduler

    /**
     * initial setup
     */
    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        scheduler = Schedulers.trampoline()
        contributionBoundaryCallback =
            ContributionBoundaryCallback(repository, sessionManager, mediaClient, scheduler);
    }

    @Test
    fun testOnZeroItemsLoaded() {
        whenever(repository.save(anyList<Contribution>()))
            .thenReturn(Single.just(listOf(1L, 2L)))
        whenever(sessionManager.userName).thenReturn("Test")
        whenever(mediaClient.getMediaListForUser(anyString())).thenReturn(
            Single.just(listOf(mock(Media::class.java)))
        )
        whenever(mediaClient.doesMediaListForUserHaveMorePages(anyString()))
            .thenReturn(true)
        contributionBoundaryCallback.onZeroItemsLoaded()
        verify(repository).save(anyList<Contribution>());
        verify(mediaClient).getMediaListForUser(anyString());
    }

    @Test
    fun testOnLastItemLoaded() {
        whenever(repository.save(anyList<Contribution>()))
            .thenReturn(Single.just(listOf(1L, 2L)))
        whenever(sessionManager.userName).thenReturn("Test")
        whenever(mediaClient.getMediaListForUser(anyString())).thenReturn(
            Single.just(listOf(mock(Media::class.java)))
        )
        whenever(mediaClient.doesMediaListForUserHaveMorePages(anyString()))
            .thenReturn(true)
        contributionBoundaryCallback.onItemAtEndLoaded(mock(Contribution::class.java))
        verify(repository).save(anyList());
        verify(mediaClient).getMediaListForUser(anyString());
    }

    @Test
    fun testOnFrontItemLoaded() {
        whenever(repository.save(anyList<Contribution>()))
            .thenReturn(Single.just(listOf(1L, 2L)))
        whenever(sessionManager.userName).thenReturn("Test")
        whenever(mediaClient.getMediaListForUser(anyString())).thenReturn(
            Single.just(listOf(mock(Media::class.java)))
        )
        whenever(mediaClient.doesMediaListForUserHaveMorePages(anyString()))
            .thenReturn(true)
        contributionBoundaryCallback.onItemAtFrontLoaded(mock(Contribution::class.java))
        verify(repository).save(anyList());
        verify(mediaClient).getMediaListForUser(anyString());
    }

    @Test
    fun testFetchContributions() {
        whenever(repository.save(anyList<Contribution>()))
            .thenReturn(Single.just(listOf(1L, 2L)))
        whenever(sessionManager.userName).thenReturn("Test")
        whenever(mediaClient.getMediaListForUser(anyString())).thenReturn(
            Single.just(listOf(mock(Media::class.java)))
        )
        whenever(mediaClient.doesMediaListForUserHaveMorePages(anyString()))
            .thenReturn(true)
        contributionBoundaryCallback.fetchContributions()
        verify(repository).save(anyList());
        verify(mediaClient).getMediaListForUser(anyString());
    }

    @Test
    fun testFetchContributionsForEndOfList() {
        whenever(sessionManager.userName).thenReturn("Test")
        whenever(mediaClient.doesMediaListForUserHaveMorePages(anyString()))
            .thenReturn(false)
        contributionBoundaryCallback.fetchContributions()
        verify(mediaClient, times(0)).getMediaListForUser(anyString())
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun testFetchContributionsFailed() {
        whenever(sessionManager.userName).thenReturn("Test")
        whenever(mediaClient.doesMediaListForUserHaveMorePages(anyString()))
            .thenReturn(true)
        whenever(mediaClient.getMediaListForUser(anyString())).thenReturn(Single.error(Exception("Error")))
        contributionBoundaryCallback.fetchContributions()
        verifyZeroInteractions(repository);
        verify(mediaClient).getMediaListForUser(anyString());
    }
}