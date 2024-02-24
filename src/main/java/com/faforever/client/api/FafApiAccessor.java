package com.faforever.client.api;

import com.faforever.client.api.dto.AchievementDefinition;
import com.faforever.client.api.dto.Clan;
import com.faforever.client.api.dto.CoopMission;
import com.faforever.client.api.dto.CoopResult;
import com.faforever.client.api.dto.FeaturedModFile;
import com.faforever.client.api.dto.Game;
import com.faforever.client.api.dto.GameReview;
import com.faforever.client.api.dto.Leaderboard;
import com.faforever.client.api.dto.LeaderboardEntry;
import com.faforever.client.api.dto.LeaderboardRatingJournal;
import com.faforever.client.api.dto.Map;
import com.faforever.client.api.dto.MapPoolAssignment;
import com.faforever.client.api.dto.MapVersion;
import com.faforever.client.api.dto.MapVersionReview;
import com.faforever.client.api.dto.MatchmakerQueue;
import com.faforever.client.api.dto.MatchmakerQueueMapPool;
import com.faforever.client.api.dto.MeResult;
import com.faforever.client.api.dto.Mod;
import com.faforever.client.api.dto.ModVersion;
import com.faforever.client.api.dto.ModVersionReview;
import com.faforever.client.api.dto.ModerationReport;
import com.faforever.client.api.dto.Player;
import com.faforever.client.api.dto.PlayerAchievement;
import com.faforever.client.api.dto.PlayerEvent;
import com.faforever.client.api.dto.Tournament;
import com.faforever.client.api.dto.TutorialCategory;
import com.faforever.client.mod.FeaturedMod;
import com.faforever.client.util.Tuple;
import com.faforever.client.vault.search.SearchController.SearchConfig;
import com.faforever.client.vault.search.SearchController.SortConfig;
import com.faforever.commons.io.ByteCountListener;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Provides access to the FAF REST API. Services should not access this class directly, but use {@link
 * com.faforever.client.remote.FafService} instead.
 */
public interface FafApiAccessor {

  List<PlayerAchievement> getPlayerAchievements(int playerId);

  List<PlayerEvent> getPlayerEvents(int playerId);

  List<AchievementDefinition> getAchievementDefinitions();

  AchievementDefinition getAchievementDefinition(String achievementId);

  void authorize(int playerId, String username, String password);

  List<Mod> getMods();

  List<com.faforever.client.api.dto.FeaturedMod> getFeaturedMods();

  List<Leaderboard> getLeaderboards();

  List<LeaderboardEntry> getAllLeaderboardEntries(String leaderboardTechnicalName);

  Tuple<List<LeaderboardEntry>, java.util.Map<String, ?>> getLeaderboardEntriesWithMeta(String leaderboardTechnicalName, int count, int page);

  List<LeaderboardEntry> getLeaderboardEntriesForPlayer(int playerId);

  List<LeaderboardRatingJournal> getRatingJournal(int playerId, int leaderboardId);

  Tuple<List<Map>, java.util.Map<String, ?>> getMapsByIdWithMeta(List<Integer> mapIdList, int count, int page);

  Tuple<List<Map>, java.util.Map<String, ?>> getMostPlayedMapsWithMeta(int count, int page);

  Tuple<List<Map>, java.util.Map<String, ?>> getHighestRatedMapsWithMeta(int count, int page);

  Tuple<List<Map>, java.util.Map<String, ?>> getNewestMapsWithMeta(int count, int page);

  List<Game> getLastGamesOnMap(int playerId, String mapVersionId, int count);

  void uploadMod(Path file, ByteCountListener listener);

  void uploadMap(Path file, boolean isRanked, List<java.util.Map<String,String>> mapDetails, ByteCountListener listener) throws IOException;

  void uploadGameLogs(Path file, String context, int id, ByteCountListener listener);

  List<CoopMission> getCoopMissions();

  List<CoopResult> getCoopLeaderboard(String missionId, int numberOfPlayers);

  void changePassword(String username, String currentPasswordHash, String newPasswordHash) throws IOException;

  ModVersion getModVersion(String uid);

  List<FeaturedModFile> getFeaturedModFiles(FeaturedMod featuredMod, Integer version);

  Tuple<List<Game>, java.util.Map<String, ?>> getNewestReplaysWithMeta(int count, int page);

  Tuple<List<Game>, java.util.Map<String, ?>> getHighestRatedReplaysWithMeta(int count, int page);

  Tuple<List<Game>, java.util.Map<String, ?>> findReplaysByQueryWithMeta(String query, int maxResults, int page, SortConfig sortConfig);

  Optional<MapVersion> findMapByTaDemoMapHash(String taDemoMapHash);

  List<MapVersion> findMapsByName(String mapDisplayName, int count, boolean includeHidden);

  List<Player> getPlayersByIds(Collection<Integer> playerIds);

  Optional<Player> queryPlayerByName(String playerName);

  GameReview createGameReview(GameReview review);

  void updateGameReview(GameReview review);

  ModVersionReview createModVersionReview(ModVersionReview review);

  void updateModVersionReview(ModVersionReview review);

  MapVersionReview createMapVersionReview(MapVersionReview review);

  void updateMapVersionReview(MapVersionReview review);

  void deleteGameReview(String id);

  List<TutorialCategory> getTutorialCategories();

  void updateReplay(String id, Game game);

  Optional<Clan> getClanByTag(String tag);

  Tuple<List<Map>, java.util.Map<String, ?>> findMapsByQueryWithMeta(SearchConfig searchConfig, int count, int page);

  Optional<MapVersion> findMapVersionById(String id);

  void deleteMapVersionReview(String id);

  void deleteModVersionReview(String id);

  Optional<Game> findReplayById(int id);

  Tuple<List<Mod>, java.util.Map<String, ?>> findModsByQueryWithMeta(SearchConfig query, int maxResults, int page);

  List<MatchmakerQueueMapPool> getMatchmakerQueueMapPools();

  List<MapPoolAssignment> getMatchmakerPoolMaps(int matchmakerQueueId, float rating);

  List<Map> getAllRankedMaps();

  Optional<MatchmakerQueue> getMatchmakerQueue(String technicalName);

  List<MatchmakerQueue> getMatchmakerQueuesByMod(String modTechnicalName);

  List<Tournament> getAllTournaments();

  List<ModerationReport> getPlayerModerationReports(int playerId);

  void postModerationReport(com.faforever.client.reporting.ModerationReport report);

  Tuple<List<MapVersion>, java.util.Map<String, ?>> getOwnedMapsWithMeta(int playerId, int loadMoreCount, int page);

  void updateMapVersion(String id, MapVersion mapVersion);

  MeResult getOwnPlayer();
}
