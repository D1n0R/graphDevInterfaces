package com.alias.teams.controller;

import com.alias.teams.model.Team;
import com.alias.teams.model.TeamMember;
import com.alias.teams.repo.TeamMemberRepository;
import com.alias.teams.repo.TeamRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamController {

    private final TeamRepository teamRepo;
    private final TeamMemberRepository memberRepo;

    public TeamController(TeamRepository teamRepo, TeamMemberRepository memberRepo) {
        this.teamRepo = teamRepo;
        this.memberRepo = memberRepo;
    }

    @PostMapping("/generate")
    public List<Team> generateTeams(
            @RequestBody List<Long> userIds,
            @RequestParam int teamsCount
    ) {
        teamRepo.deleteAll();
        memberRepo.deleteAll();

        Collections.shuffle(userIds);

        List<Team> teams = teamRepo.saveAll(
                java.util.stream.IntStream.range(0, teamsCount)
                        .mapToObj(i -> new Team("Team " + (i + 1)))
                        .toList()
        );

        for (int i = 0; i < userIds.size(); i++) {
            Team team = teams.get(i % teamsCount);
            memberRepo.save(new TeamMember(userIds.get(i), team));
        }

        return teams;
    }

    @PostMapping("/{id}/score")
    public void addScore(@PathVariable Long id) {
        Team team = teamRepo.findById(id).orElseThrow();
        team.incrementScore();
        teamRepo.save(team);
    }

    @GetMapping
    public List<Team> teams() {
        return teamRepo.findAll();
    }
}
