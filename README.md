# Webhook Interceptor

Add something fancy to your webhook calls before sending them off to their final destination.

Configured by default to attach images from your local Jellyfin server before sending them to your Discord webhook. More features coming soon!

## Quickstart

Supply your webhook URL in an environment variable called `WEBHOOK_URL` and run the application.

### Docker Compose

```yaml
services:
  interceptor:
    image: ghcr.io/allegracodes/webhook-interceptor:latest
    container_name: interceptor
    environment:
      - WEBHOOK_URL=${MY_WEBHOOK_URL}
    ports:
      - 8080:8080
    restart: unless-stopped
```

## Configure

Configure the behavior using environment variables

`WEBHOOK_URL` - Required. The destination of the processed request.

`MATCH_PATTERN` - Regex pattern to select which tokens to replace. Defaults to `^.*(/Images/|/UserImage|/Branding/Splashscreen).*$`

`REPLACEMENT_STRING` - String to replace the selected tokens. Defaults to `image`

`ADD_INDEX` - Boolean to indicate if you'd like to add a unique index number after the replacement string. Defaults to `true`

`REPLACEMENT_PREFIX` - Prefix to prepend to the replacement string. Defaults to `attachment://`

`REPLACEMENT_SUFFIX` - Suffix to append to the replacement string. Defaults to `.jpg`

`QUERY_PARAMS` - Query parameters for fetching files. Defaults to `?format=Jpg`

## Future Enhancements

Please open an issue to suggest more ideas!

Current plans:

- [ ] use github actions to build and publish
- [ ] implement jellyfin picture attachers for more webhooks
- [ ] allow execution of custom scripts

## Acknowledgements

- Thanks to the [Jellyfin](https://jellyfin.org/) team for their great work that inspired this project
