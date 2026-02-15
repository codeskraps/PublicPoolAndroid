# API Endpoints

All HTTP calls go through `data/remote/KtorApiService.kt`. The Ktor client is configured with `ignoreUnknownKeys = true` and `expectSuccess = true`.

## Public Pool API

Base URL: user-configurable, default `https://public-pool.io:40557/api`. Stored in DataStore under `BASE_URL`.

| Endpoint | Method | Returns |
|----------|--------|---------|
| `/client/{walletAddress}` | GET | `ClientInfoDto` — best difficulty, worker count, worker list |
| `/network` | GET | `NetworkInfoDto` — network difficulty, hash rate, block height/weight |
| `/client/{walletAddress}/chart` | GET | `List<ChartDataPointDto>` — 10-minute hash rate data points |

## Blockchain.info

Base URL: `https://blockchain.info` (hardcoded)

| Endpoint | Method | Returns |
|----------|--------|---------|
| `/address/{walletAddress}?format=json` | GET | `WalletInfoDto` — balance, transactions, sent/received totals |

## Binance

Base URL: `https://api.binance.com` (hardcoded)

| Endpoint | Method | Returns |
|----------|--------|---------|
| `/api/v3/ticker/price?symbol={symbol}` | GET | `BinanceTickerDto` — current price. Default symbol: `BTCUSDT` |

## DTOs

All DTOs live in `data/remote/dto/` and are `@Serializable`. They map to domain models via extension functions in `data/mappers/Mappers.kt`.

## Error Handling

The Ktor client has `expectSuccess = true`, so non-2xx responses throw exceptions. Use cases wrap API calls in `runCatching` and return `Result<T>`. ScreenModels handle `Result.onSuccess` / `Result.onFailure` to update state and emit error effects.
