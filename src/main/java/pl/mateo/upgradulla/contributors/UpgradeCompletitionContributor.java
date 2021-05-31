package pl.mateo.upgradulla.contributors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.DumbAware;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import pl.mateo.upgradulla.entities.Entity;
import pl.mateo.upgradulla.services.UpgradeKnowledgeBaseService;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * https://www.plugin-dev.com/intellij/custom-language/code-completion/
 *
 *
 */
public class UpgradeCompletitionContributor extends CompletionContributor implements DumbAware {

    private UpgradeKnowledgeBaseService upgradeKnowledgeBaseService;

    public UpgradeCompletitionContributor() {

        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(PsiIdentifier.class).withParent(PsiReferenceExpression.class), new UpgradeCompletionProvider());
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(PsiIdentifier.class).withParent(PsiJavaCodeReferenceElement.class), new UpgradeCompletionProvider());
    }

    private class UpgradeMatcher extends PrefixMatcher {

        public UpgradeMatcher(String prefix) {
            super(prefix);
        }

        @Override
        public boolean prefixMatches(@NotNull String name) {
            return true;
        }

        @Override
        public @NotNull PrefixMatcher cloneWithPrefix(@NotNull String prefix) {
            return null;
        }
    }

    private class UpgradeCompletionProvider extends CompletionProvider<CompletionParameters> {

        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
            {
                String codeCompletion = parameters.getPosition().getContext().getText();
                List<String> suggestions = null;
                try {
                    suggestions = getUpgradeSuggestions(codeCompletion);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<String> testSuggestions = Arrays.asList("DeprecatedClassXXX", "DeprecatedClassYYY");
                suggestions.add("DeprecatedClassXXX");
                suggestions.add("DeprecatedClassYYY");
                // suggestions.stream().forEach(entry -> resultSet.addElement(LookupElementBuilder.create(entry)));
                suggestions.stream().forEach(entry ->
                {
                    result.withPrefixMatcher(new UpgradeMatcher(entry)).addElement(LookupElementBuilder.create(entry));
                });
            }
        }
    }

    private List<String> getUpgradeSuggestions(String input) throws URISyntaxException, IOException, InterruptedException {
        String queryText = input.replace("IntellijIdeaRulezzz", "");
        // getUpgradeKnowledgeBaseService().query(queryText);
        return httpGetRequest(queryText);
    }

    public static List<String> httpGetRequest(String searchTerm) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .uri(URI.create("http://localhost:8080/query?" + searchTerm))
                .headers("Accept-Enconding", "gzip, deflate")
                .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        String responseBody = response.body();
        int responseStatusCode = response.statusCode();

        Entity itemWithOwner = new ObjectMapper().readValue(responseBody, Entity.class);

        System.out.println("httpGetRequest: " + itemWithOwner.getEmbedded().getQuery().get(0).getTermReplacement());
        System.out.println("httpGetRequest status code: " + responseStatusCode);
        return itemWithOwner.getEmbedded().getQuery().stream().map(query -> query.getTermReplacement()).collect(Collectors.toList());
    }

    public UpgradeKnowledgeBaseService getUpgradeKnowledgeBaseService() {
        return upgradeKnowledgeBaseService;


    }

}

