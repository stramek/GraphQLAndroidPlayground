# GraphQLAndroidPlayground

## Intro
Example usage of Apollo library for Android using Github API.

This is simple example how to use GraphQL in Android using
- default callbacks,
- rxJava2,
- coroutines.

## Setup

- Replace `YOUR_GITHUB_API_KEY` with your github API key inside app `build.gradle`

## Additional info

To download your own `schema.json` follow instructions:
1) `npm install -g apollo`
2) `apollo schema:download --header "Authorization: Bearer API_KEY” --endpoint=https://api.github.com/graphql /path/to/file/schema.json`
