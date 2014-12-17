# Introduction

Build block chain applications easily with our web APIs and callbacks. Run them reliably on our cloud-optimized platform. High throughputs, linear scaling, low-latency. Over 99.99% up time. No single point of failure.

Learn more at http://www.blockcypher.com

# Group Blockchain

## Chain [/v1/{coin}/{chain}?token={token}]

+ Parameters

  + coin (required, string, `btc`) name of the coin to use
  + chain (required, string, `test3`) name for the desired coin

+ Model
  + Body

            {
              "name": "BTC.main",
              "height": 328555,
              "hash": "000000000000000011ca6426165101ea682e9432383a10d7877dce5086c9be06",
              "time": "2014-11-04T19:59:06.351467627Z",
              "latest_url": "https://api.blockcypher.com/v1/btc/main/blocks/000000000000000011ca6426165101ea682e9432383a10d7877dce5086c9be06",
              "previous_hash": "000000000000000008d18c15c50cab09c3afff717bd062507108616b7b82dba2",
              "previous_url": "https://api.blockcypher.com/v1/btc/main/blocks/000000000000000008d18c15c50cab09c3afff717bd062507108616b7b82dba2",
              "peer_count": 253,
              "unconfirmed_count": 3994,
              "last_fork_height": 328330,
              "last_fork_hash": "00000000000000001a246a765a3204f4fdf9ca4cfa3c6c5856801b8b0d08ab86"
            }

### Retrieve the Latest Chain [GET]
+ Response 200

    [Chain][]

+ Response 404

            {"error": "Unknown coin or chain: {coin}/{chain}"}

## Block [/v1/{coin}/{chain}/blocks/{hash,height}?token={token}]

+ Parameters

  + hash (required, string, `000000000000000011ca6426165101ea682e`) of the desired block
  + height (required, number, `328555`) at which the desired block made it into the chain

+ Model
  + Body

            {
              "hash": "0000000000000000189bba3564a63772107b5673c940c16f12662b3e8546b412",
              "height": 294322,
              "chain": "BTC.main",
              "total": 1146783914,
              "fees": 130999,
              "ver": 2,
              "time": "2014-04-05T07:49:18Z",
              "received": "2014-04-05T07:49:30.981Z",
              "bits": 419486617,
              "nonce": 1225187768,
              "n_tx": 10,
              "prev_block": "0000000000000000ced0958bd27720b71d32c5847e40660aaca39f33c298abb0",
              "mrkl_root": "359d624d37aee1efa5662b7f5dbc390e996d561afc8148e8d716cf6ad765a952",
              "txids": [
                "32b3b86e40d996b1f281e24e8d4af2ceacbf874c4038369cc21baa807409b277",
                "1579f716359ba1a207f70248135f5e5fadf539be1dcf5300613aedcb6577d287",
                "dd1f183348eb41eaaa9ecf8012f9cca3ecbae41a6349f0cc4bfd2b1a497fa3d0",
                "749d12ccd180968b82aef4c271ca4effdf981d9b5d12523264457c9d4e6fa78e",
                "c4fe2ee16b8e3067d3d95caf7944011f4959781288b807df8bf853b7f80ed97c",
                "5a2114675265522d2b7ce8a7874cfa7a22ccc3fb6566a8599d6432c6805b1b5f",
                "077d851c8240671de80caa8be9f5285201c08a70edc5a45a9cd35fe7eaebf5e1",
                "6202cc55fbd9130e065c9294a5b2e061c26f3d2c8df56c32da605d9f183103f9",
                "ad3e7aa1c33f1d3e1c105d94f7b1542808da07bbe66b9621b050104a85dbf650",
                "36cc61016b9d1bd69768666f287db1edaa9b292fb442f152af7099305677230e"
              ],
              "prev_block_url": "http://api.blockcypher.com/v1/btc/main/blocks/0000000000000000ced0958bd27720b71d32c5847e40...",
              "tx_url": "http://api.blockcypher.com/v1/btc/main/txs/"
            }

### Retrieve a Block [GET]
+ Response 200

    [Block][]

+ Response 404

            {"error": "Unknown block: {hash|height}"}

## Transaction [/v1/{coin}/{chain}/txs/{hash}?token={token}]

+ Parameters

  + hash (required, string, `ce6115d57a00d69ed0`) of the desired transaction

+ Model
  + Body

            {
              "block_hash": "00000000e2f1214e0d7bb269ee19f9857d197050b64317e6df336b914beb3b95",
              "block_height": 307074,
              "hash": "ce6115d57a00d69ed04766a68680db47f54f4821f299f65aa53c181371981002",
              "addresses": [
                "mhW9PYb5jsjpsS5x6dcLrZj7gPvw9mMb9c"
              ],
              "total": 107950000,
              "fees": 10000,
              "preference": "medium",
              "relayed_by": "54.165.200.137:18333",
              "confirmed": "2014-11-08T02:10:31Z",
              "received": "2014-11-08T00:17:18.137485772Z",
              "ver": 1,
              "lock_time": 0,
              "double_spend": false,
              "vin_sz": 1,
              "vout_sz": 1,
              "confirmations": 4,
              "inputs": [
                {
                  "prev_hash": "929d737f057ff9b8d3b0020f64ec33124b57b5d03305d1d3d95e4bf80b843f89",
                  "output_index": 0,
                  "script": "47304402202efe5602d3308549805d026534d50221f1e0141cc71c803b5d8e719c9d4d95320220734933c1934e9083dd82140271f0b6e0302e6c4d82779c59720c553bf59dad95012102be99138b48b430a8ee40bf8b56c8ebc584c363774010a9bfe549a87126e61746",
                  "output_value": 107960000,
                  "addresses": [
                    "mhW9PYb5jsjpsS5x6dcLrZj7gPvw9mMb9c"
                  ],
                  "script_type": "pay-to-pubkey-hash"
                }
              ],
              "outputs": [
                {
                  "value": 107950000,
                  "script": "76a91415c918d389673c6cd0660050f268a843361e111188ac",
                  "spent_by": "",
                  "addresses": [
                    "mhW9PYb5jsjpsS5x6dcLrZj7gPvw9mMb9c"
                  ],
                  "script_type": "pay-to-pubkey-hash"
                }
              ]
            }

### Retrieve a Transaction [GET]
+ Response 200

    [Transaction][]

+ Response 404

            {"error": "Unknown transaction: {hash}"}


## Transaction Confidence [/v1/{coin}/{chain}/txs/{hash}/confidence?token={token}]

+ Parameters

  + hash (required, string, `ce6115d57a00d69ed0`) of the desired transaction

+ Model
  + Body

            {
              "age_seconds": 28.830694006,
              "receive_count": 400,
              "confidence": 0.9978059303207656,
              "txhash": "d7c0e09f3fd744098b681872a1f176b2e9c217e0aa98eadb3e4a106efb928cbf",
              "txurl": "https://api.blockcypher.com/v1/btc/main/txs/d7c0e09f3fd744098b681872a1f176b2e9c217e0aa98eadb3e4a106efb928cbf"
            }

### Retrieve the Transaction Confidence [GET]
+ Response 200

    [Transaction Confidence][]

+ Response 404

            {"error": "Unknown transaction: {hash}"}


# Group Payments

## Payment Collection [/v1/{coin}/{chain}/payments?token={token}]

+ Parameters

  + coin (required, string, `btc`) name of the coin to use
  + chain (required, string, `test3`) name for the desired coin
  + token (required, string, `xyz`) your API token

+ Model
  + Body

            {
              "id": "399d0923-e920-48ee-8928-2051cbfbc369",
              "input_address": "16uKw7GsQSzfMaVTcT7tpFQkd7Rh9qcXWX",
              "destination": "15qx9ug952GWGTNn7Uiv6vode4RcGrRemh",
              "callback_url": "https://my.domain.com/callbacks/payments",
              "token": "..."
            }

### Create New Payment [POST]
+ Response 200

    [Payment Collection][]

+ Response 400

            {"error": "{text}"}

### List Created Payments [GET]
+ Response 200

    [Payment Collection][]

+ Response 400

            {"error": "{text}"}

## Payment [/v1/{coin}/{chain}/payments/{id}?token={token}]

+ Parameters

  + id (required, string, `165b6f10-95a1-4a3b-b6ab-589e53af925b`) payment id
  + token (required, string, `xyz`) your API token

+ Model
  + Body

            {
              "id": "399d0923-e920-48ee-8928-2051cbfbc369",
              "input_address": "16uKw7GsQSzfMaVTcT7tpFQkd7Rh9qcXWX",
              "destination": "15qx9ug952GWGTNn7Uiv6vode4RcGrRemh",
              "callback_url": "https://my.domain.com/callbacks/payments",
              "token": "..."
            }

### Get Payment [GET]
+ Response 200

    [Payment][]

+ Response 400

            {"error": "{text}"}

### Delete Payments [DELETE]
+ Response 204

+ Response 400

            {"error": "{text}"}

# Group Building Transactions

## Transaction Build [/v1/{coin}/{chain}/txs/new?token={token}]

+ Parameters

  + token (required, string, `xyz`) your API token

+ Model
  + Body

            {
              "inputs": [
                {"addresses": ["181w71oR7nTEsKducLBTUJkdbTvLDQzymL"]}
              ],
              "outputs": [
                {"addresses": ["1FGAsJFNgWvFz2tWQAnRq8S6fVX9Zmuxje"], "value": 4000000000}
              ]
            }

### Build New Transaction [POST]
+ Response 200

            {
              "tx": { ... }
              "tosign": [
                "04779733bba8085dd86c21d86c8f9e786a1124751c42061d4c539229a07c4464",
                "0396ea6f1bf7493e738339bd720267e7281144b222343ffe4167e00792c62ff2"
              ]
            }

+ Response 400

            {
              "errors": [...]
              "tx": { ... }
            }

## Transaction Send [/v1/{coin}/{chain}/txs/send?token={token}]

+ Parameters

  + token (required, string, `xyz`) your API token

+ Model
  + Body

            {
              "tx": { ... }
              "tosign": [...]
              "signatures": [...]
              "pubkeys": [...]
            }

### Send New Transaction [POST]
+ Response 200

            {
              "tx": { ... }
            }

+ Response 400

            {
              "errors": [...]
              "tx": { ... }
            }

## Transaction Push [/v1/{coin}/{chain}/txs/push?token={token}]

+ Parameters

  + token (required, string, `xyz`) your API token

+ Model
  + Body

            {
              "tx": "1adb9867..."
            }

### Push Raw Transaction [POST]
+ Response 200

            {
              "tx": { ... }
            }

+ Response 400

            {
              "errors": [...]
              "tx": { ... }
            }

# Group Addresses

## Address Collection [/v1/{coin}/{chain}/addrs?token={token}]

+ Parameters

  + token (required, string, `xyz`) your API token

+ Model
  + Body

            {
              "private": "07f6f3dd1f383a8e2085a031f6f3e5183145f3fa1080a5a7f3f39fee981ddd5b",
              "public": "039920d51829763242314cb61f6bbd69e8daa71e1909f66cf0daa2adf555db29ed",
              "address": "181w71oR7nTEsKducLBTUJkdbTvLDQzymL"
            }

### New Address [POST]
+ Response 200

    [Address Collection][]

## Address [/v1/{coin}/{chain}/addrs/{hash}?token={token}]

+ Parameters

  + hash (required, string, `181w71oR7nTEsKducLBTUJkdbTvLDQzymL`) the address string

+ Model
  + Body

            {
              "address": "1AnrxKgzq1wWyzT81SmXDpJbFfXYtpVmmC",
              "balance": 0,
              "unconfirmed_balance": 0,
              "final_balance": 0,
              "n_tx": 2,
              "unconfirmed_n_tx": 0,
              "final_n_tx": 2,
              "txrefs": [
                {
                  "tx_hash": "9a1cb63138b49b2d24b194522e6c7d2792dd854f110f50bab021ef37e7ba272f",
                  "block_height": 327528,
                  "tx_input_n": 0,
                  "tx_output_n": -1,
                  "value": 3507206897,
                  "spent": false,
                  "confirmations": 1635,
                  "confirmed": "2014-10-29T16:54:09Z",
                  "double_spend": false
                },
                {
                  "tx_hash": "aed61a810c63fd81d586ec47422656f6b02ecf87dd360dffc11aa1fc0597fa08",
                  "block_height": 327526,
                  "tx_input_n": -1,
                  "tx_output_n": 1,
                  "value": 3507206897,
                  "spent": true,
                  "spent_by": "9a1cb63138b49b2d24b194522e6c7d2792dd854f110f50bab021ef37e7ba272f",
                  "confirmations": 1637,
                  "confirmed": "2014-10-29T16:23:38Z",
                  "double_spend": false
                }
              ],
              "tx_url": "https://api.blockcypher.com/v1/btc/main/txs/"
            }

### Get Address [GET]
+ Response 200

    [Address][]

+ Response 404

            {"error": "Address not found"}

## Address Balance [/v1/{coin}/{chain}/addrs/{hash}/balance?token={token}]

+ Parameters

  + hash (required, string, `181w71oR7nTEsKducLBTUJkdbTvLDQzymL`) the address string

+ Model
  + Body

            {
              "address": "1DEP8i3QJCsomS4BSMY2RpU1upv62aGvhD",
              "balance": 4433416,
              "unconfirmed_balance": 0,
              "final_balance": 4433416,
              "n_tx": 7,
              "unconfirmed_n_tx": 0,
              "final_n_tx": 7
            }

### Get Address [GET]
+ Response 200

    [Address Balance][]

+ Response 404

            {"error": "Address not found"}

# Group Notifications

## Webhook Collection [/v1/{coin}/{chain}/hooks?token={token}]

+ Parameters

  + token (required, string, `xyz`) your API token

+ Model
  + Body

            {
              "id": "399d0923-e920-48ee-8928-2051cbfbc369",
              "event": "new-block",
              "url": "https://my.domain.com/callbacks/new-block"
              "token": "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            }

### Create Webhook [POST]
+ Response 200

    [Webhook Collection][]

### List Webhook [GET]
+ Response 200

    [Webhook Collection][]

## Webhook [/v1/{coin}/{chain}/hooks/{id}?token={token}]

+ Parameters

  + id (required, string, `399d0923-e920-48ee-8928-2051cbfbc369`) the webhook id

+ Model
  + Body

            {
              "id": "399d0923-e920-48ee-8928-2051cbfbc369",
              "event": "new-block",
              "url": "https://my.domain.com/callbacks/new-block"
              "token": "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            }

### Get Webhook [GET]
+ Response 200

    [Webhook][]

+ Response 404

            {"error": "WebHook not found"}

### Delete Webhook [DELETE]
+ Response 204

+ Response 404

            {"error": "WebHook not found"}

