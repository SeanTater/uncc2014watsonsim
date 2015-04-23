package edu.uncc.cs.watsonsim;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.stanford.nlp.util.Pair;
import edu.uncc.cs.watsonsim.nlp.ClueType;

public class LATDetectionTest {

	@Test
	public void testSimpleFetchLAT() {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Pair<String,String>[] cases = (Pair<String, String> []) new Pair[] {
				new Pair("man", "This man was the first to walk on the moon."),
				new Pair("number", "Palm Sunday occurs this number of days before Easter."),
				new Pair("giants", "A challenge to any optician, these giants had only one eye."),
				new Pair("university", "This university's Orangemen routed Clemson 41-0 in the Jan. 1, 1996 Gator Bowl."),
				new Pair("country", "A problem for this African country is that much of its mail is mistakenly sent to Switzerland."),
				
				// Parses wrong - so LAT has no chance
				//!new Pair("classic", "Coca-Cola introduced its first diet soda, this classic with a 3-letter name."),
				new Pair("", "French for \"very much\" or \"very many\", it often comes after \"merci\"."),
				new Pair("fruit", "The name of this small Oriental citrus fruit is from the Chinese for \"golden orange\"."),
				new Pair("play", "Alabama's official outdoor drama is this play, enacted in the summer at Helen Keller's birthplace."),
				new Pair("group", "This singing group is featured on a video subtitled \"One Hour Of Girl Power!\"."),
				//!new Pair("sheep", "A wee female sheep can grow up to be a great big one"),
				
				
				//!new Pair("story", "His famous story \"The Tell-Tale Heart\" tells us, \"It was not the old man who vexed me, but his evil eye\""),
				new Pair("phenomenon", "The ball form of this weather phenomenon is also known as kugelblitz"),
				new Pair("shoes", "These shoes are advertised with the slogan, \"Looks Like a Pump, Feels Like a Sneaker!\""),
				new Pair("story", "This 1895 story set in the future was H.G. Wells' first novel"),
				new Pair("activity", "Normally this activity is harmless, but make sure the person doesn't fall down the stairs"),
				new Pair("man", "The airport formerly known as Idlewild is now named for this man"),
				new Pair("strongman", "This strongman was killed destroying a Philistine temple & was interred in his father's burying place"),
				new Pair("", "In it, Paul Reubens' alter ego joins the circus"),
				new Pair("name", "In 1914 the once & future St. Petersburg got this new name; 10 years later it became Leningrad"),
				//!new Pair("", "Shielded by a car, in 1941 Alf Letourner hit a speed over 108 mph on this"),
				new Pair("", "1980: \"Hit Me With Your Best Shot\""),
				new Pair("", "The \"Great Awakening\" of the 1740s refers to this"),
				new Pair("", "Paris is served by both Orly and an airport named for him"),
				new Pair("mountain", "When Queen Jezebel vowed to execute Elijah, he fled to this holy mountain"),
				//!new Pair("island", "Picturesque Ionian island that was the birthplace of Britain's Prince Philip"),
				//debatable
				//!new Pair("pear", "It's the most widely cultivated variety of pear in the U.S. -- you can quote us on that"),
				new Pair("city", "This Japanese city was modeled on \"Cha'ng-an\", the T'ang Dynasty's  Chinese capital"),
				//!new Pair("wine", "For its colour & the band that once recorded there, Chateau Miraval calls a wine of this type \"Pink Floyd\""),
				new Pair("lawman", "This Gene Barry lawman wore a derby & fancy clothes & carried a gold-topped cane"),
				//!new Pair("ticket", "Someone who votes for candidates of different parties on the same ballot votes this kind of \"ticket\""),
				new Pair("woman", "Illustrations of this nursery rhyme woman probably inspired the loose dress named for her"),
				// plural?
				//!new Pair("widows", "One of the two presidents' widows who remarried"),
				new Pair("", "She won a degree from the Univ. of VT. in 1902 & was the 1st grad of a co-ed college to be first lady"),
				new Pair("prince", "This prince's mother, Princess Alice of Battenberg, was Lord Mountbatten's sister"),
				new Pair("word", "This word can precede \"Biography\" or follow \"Humboldt\""),
				new Pair("", "From an old word for \"cape\", it's an older person who, for propriety's sake, accompanies young unmarrieds"),
				new Pair("choreographer", "Stravinsky & this Russian-born American choreographer collaborated on several ballets including \"Agon\""),
				new Pair("salad", "Toss together some lettuce, olives, cucumbers & feta cheese & you've got this ethnic salad"),
				new Pair("virus", "Between the ages of 1 & 10, about 95% of U.S. children get this virus, also called varicella"),
				new Pair("town", "On the Catholic calendar, Dec. 28 is Childermas, remembering the slaughter of the innocents in this biblical town"),
				new Pair("date", "In Latin America the Christmastime walks & visits called novenas start on this pre-Christmas date"),
				new Pair("king", "William was born in 1650, the year after this king, his maternal grandfather, was beheaded"),
				new Pair("", "His rescue of 2 EDS employees from a prison in Iran was the subject of Ken Follett's \"On Wings of Eagles\""),
				new Pair("metal", "Crowns put on front teeth are often made of porcelain; those put on back teeth, of this precious metal"),
				// debatable
				new Pair("branch", "Only one medal has been awarded to a member of this service branch"),
				new Pair("", "A classic: \"Good Golly, Miss ____\""),
				//!new Pair("number", "Number of months that do not have 31 days"),
				new Pair("drama", "Boris Karloff did \"The Raven\" with Jack Nicholson, who was in this Navy courtroom drama with Kevin"),
				new Pair("state", "You can visit the infamous Andersonville Civil War prison, a national historic site, in this state"),
				new Pair("film", "The title tune to this James Dean film is subtitled \"This Then is Texas\""),
				new Pair("capital", "Saint Adalbert, the \"Apostle of the Prussians\", became bishop of this Czech capital in 982"),
				new Pair("occupation", "Police officers rank No. 8; this occupation \"heats up\" at No. 2"),
				new Pair("", "The London traffic junction & meeting place seen here"),
				new Pair("city", "Sophia Loren was raised near this large city that's 14 miles from Pompeii"),
				//!new Pair("playwright", "Spanish playwright who set his 1936 play in \"The House of Bernarda Alba\""),
				new Pair("", "It defies the natural order of things to open it indoors, & it acts as a screen against good fortune"),
				// debatable
				new Pair("", "In 1923 he was an obscure major stationed in Panama; 30 years later he was U.S. president"),
				//!new Pair("hit", "Phil Spector overdubbed this last number-one Beatles hit; John and Paul's original version is not for sale"),
				//!!new Pair("booklet", "From the Greek for \"beloved of all\", it's a small paper booklet handed out at meetings & rallies"),
				new Pair("man", "The year after \"Meet Me in St. Louis\" was released Judy married this man who directed it"),
				new Pair("fungus", "Ancient Romans prized the shaggy mane, an ink cap type of this fungus"),
				new Pair("film", "This 1994 film about 2 drag queens & a transsexual featured the music of ABBA"),
				new Pair("capital", "The name of this capital is probably from Sinhalese for \"port\" or \"ferry\""),
				// This actually has a typo from the original source
				//!new Pair("painting", "The most famous \"Sunday Afternoon\" this pointillist painted was on the \"Grand Jatte\""),
				//!new Pair("island", "Island off New Jersey or Argentina, but Argentines call it \"Isla de los Estados\""),
				new Pair("", "When he ran for president in 1884, the Democrats called him the \"Continental Liar From the State of Maine\""),
				new Pair("name", "This alternate name for a werewolf is from the Greek for \"wolf man\""),
				new Pair("country", "Casimir IV was succeeded as king of this country by his son John Albert"),
				new Pair("mountain", "This tallest Greek mountain is situated on the border between Thessaly & Macedonia"),
				new Pair("city", "In 1923 Angora, now called Ankara, succeeded this city as its country's capital"),
				new Pair("", "Carlsbad's Callaway makes hi-tech these, like the FT Optiforce"),
				new Pair("", "According to NFL GameDay magazine, the Redskins have 58,300 names on a waiting list for these"),
				new Pair("cluster", "Alcyone is the brightest star in this star cluster sometimes called the Seven Sisters"),
				//!new Pair("\"G\"-man", "In the 1920s this \"G\"-man opened a leather goods shop on the Via Vigna Nuova"),
				new Pair("substance", "Both Joseph in the Old Testament & Jesus in the New were \"sold\" for this substance"),
				new Pair("", "I played football at Florida State & used my # 22 in all my movies, like \"Semi-Tough\""),
				new Pair("", "Modern miracle method: I.V.F."),
				new Pair("pioneer", "This modern dance pioneer lost her two children when they drowned in a 1913 auto accident"),
				new Pair("fiber", "Count Hilaire de Chardonnet is considered the father of this regenerated cellulose fiber"),
				//!new Pair("city", "Russian city where Nadia Comaneci  competed in her second Olympics"),
				new Pair("", "It used to be a student who kept order in the halls; now it's a screen for your computer"),
				new Pair("war", "Most of the fighting during this war in the 1850s took place near the Russian port of Sevastopol"),
				//!!new Pair("", "The Swedes who settled in Delaware in 1638 were the 1st in America to build cabins made of these"),
				new Pair("pesticide", "The bald eagle population, now recovering, took a big dive in the 1940s due to this pesticide"),
				//!!new Pair("airline", "It's the aptly named state-owned airline of Greece"),
				new Pair("", "In 1983, the year he became a U.S. citizen, he flexed his acting muscles making \"Conan the Destroyer\""),
				// quotes?
				new Pair("feud", "One of this comedian's running gags was this \"feud\" with Fred Allen"),
				new Pair("", "He was the creator & behind-the-scenes master of the Muppets"),
				new Pair("frontiersman", "Pat Boone descends from this famed frontiersman"),
				//!!new Pair("", "Designer Anne Klein chose a lion's face as her company's symbol because this was her sign"),
				new Pair("city", "This city's Nov. 9, 1965 blackout began at 5:27 P.M., stranding subway riders & darkening Herald Square"),
				//!new Pair("group", "The video of the song heard here won this group 4 MTV Video Awards in 1997: (\"Virtual Insanity\")"),
				new Pair("series", "In October 1996 Anthony LaPaglia joined the cast of this series as wily attorney Jimmy Wyler"),
				// quotes?
				//!new Pair("modeling compound", "Introduced in the 1950s, this \"modeling compound\" began as a cleaning product for wallpaper"),
				new Pair("treat", "This sweet treat is the subject of \"wars\" in a Food Network show judged by Candace Nelson, owner of Sprinkles Bakery"),
				new Pair("", "For 2 months in 1784, it served as the temporary capital of the U.S.; it became New Jersey's capital in 1790"),
				new Pair("", "Camera type: S.L.R."),
				new Pair("state", "The first chair factory in Thomasville in this state opened around 1870"),
				new Pair("arthropod", "Like their mammal counterparts, wolf species of this arthropod often live in burrows & hunt prey"),
				new Pair("", "His monumental collection \"La Comedie Humaine\" encompasses about 90 novels & stories"),
				new Pair("", "In 2011 & 2013 she was Smurfette"),
				new Pair("", "Robert Frost wrote, it \"Is the place where, when you have to go there, they have to take you in\""),
				//!new Pair("city", "City with Caribbean's largest harbor, it's on the largest Caribbean island"),
				//!new Pair("partner", "Detective Sam Spade's partner whose death sent Bogart in pursuit of the Maltese Falcon"),
				new Pair("man", "Clint Eastwood won for directing \"Unforgiven\" & this man won for acting in it"),
				// plural?
				new Pair("appendages", "Unlike the octopus or squid, the nautilus may have up to 90 of these armlike appendages"),
				new Pair("", "It advertises its product as \"The Pfabulous Pfaucet with the Pfunny Name\""),
				new Pair("country", "The czardas is the national dance of this country & Liszt used it as a basis for his rhapsodies"),
				new Pair("", "\"Mr. Pennypacker\", \"Mr. Scoutmaster\", and \"Mr. Belvedere\" are some of his title characters"),
				new Pair("", "Their name comes from Greek for \"to immerse\", a rite they usually reserve for adults affirming faith in Jesus  "),
		};
		for (Pair<String, String> pair : cases)
			assertEquals(pair.first, 
					ClueType.fromClue(pair.second)
					);
	}

}
