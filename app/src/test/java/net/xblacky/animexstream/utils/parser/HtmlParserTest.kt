package net.xblacky.animexstream.utils.parser

import com.google.common.truth.Truth.assertThat
import net.xblacky.animexstream.Responses
import net.xblacky.animexstream.utils.model.EpisodeInfo
import org.junit.Before
import org.junit.Test

class HtmlParserTest {

    private lateinit var responses: Responses

    @Before
    fun setup() {
        responses = Responses
    }

    @Test
    fun `parse recent sub or dub test returning correct model list`() {
    }

    @Test
    fun `parse popular test returning correct model list`() {

    }

    @Test
    fun `parse Movie test returning correct model list`() {

    }

    @Test
    fun `parse Anime Info test returning correct result`() {

    }

    @Test
    fun `parse Media Url test returning correct URL`() {
//        val episodeInfo = HtmlParser.parseMediaUrl(responses.AnimeUrlString)
//        assertThat(episodeInfo).isInstanceOf(EpisodeInfo::class.java)
//        assertThat(episodeInfo.vidcdnUrl).isEqualTo("https://gogo-play.net/download?id=MTU0MjAw&amp;typesub=Gogoanime-DUB&amp;title=Rifle+Is+Beautiful+%28Dub%29+Episode+10")
    }

    @Test
    fun `parse Streaming Url test returning correct URL`() {
        val link = HtmlParser.parseStreamingUrl(responses.StreamingUrlString)
        assertThat(responses.StreamingUrlString).isNotEmpty()
        assertThat(responses.StreamingUrlString).isNotNull()
        assertThat(link).isEqualTo("https://storage.googleapis.com/eco-carver-306016/DD4_IOMT14A1/23a_1615198952154126.mp4")
    }

    @Test
    fun `filter episode list test returning correct list`() {

    }

    @Test
    fun `filter genre name test filtering correct genres`() {

    }

    @Test
    fun `get genre list test returning correct list`() {

    }

    @Test
    fun `get format info values test returning correct test result`() {

    }

    @Test
    fun `get category URL test returning correct URL`() {

    }
}