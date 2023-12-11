# DCC_Service Backend Api

This API exposes endpoints to manage DCC_Service Backend.

**Contact information:**

Physikalisch-Technische Bundesanstalt

https://www.ptb.de

Daniel.Hutzschenreuter@ptb.de

> version 1.0.0

Base URL: https://d-si.ptb.de/api/d-dcc

## DCC_Controller:

DCC Controller with endpoints: /api/d-dcc

## POST Request: addDcc

Add a new DCC to the Database.

Request URL: https://d-si.ptb.de/api/d-dcc/addDcc

> Example Body Payload

```json
{
  "pid": "CCM.M-K1-NPL9507",
  "xmlBase64": "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4KPGRjYzpkaWdpdGFsQ2FsaWJyYXRpb25DZXJ0aWZpY2F0ZSB4bWx",
  "isDccValid": true
}
```

### Payload

| Name | Type | Required |
|------|------|----------|
| body | body | Dcc      |

#### Response Examples

> 201 Response message

```
  "Dcc successful created"
```

> 400 Response message

```
"pid already exist"
```

### Responses

| HTTP Status Code | Meaning | Description          |
|------------------|---------|----------------------|
| 201              | OK      | created              |
| 400              | ERROR   | bad request/conflict |

## GET Request: getPidList

Get available pidList of DCC stored in the database.

Request URL: https://d-si.ptb.de/api/d-dcc/dccPidList

#### Response Example

> 200 Response

```json
[
  "https://d-si.ptb.de/d-dcc/dcc/CCM.M-K1-KRISS9703",
  "https://d-si.ptb.de/d-dcc/dcc/CCM.M-K1-NPL9507",
  "https://d-si.ptb.de/d-dcc/dcc/CCM.M-K1-PTB9608",
  "https://d-si.ptb.de/d-dcc/dcc/CCM.M-K1-BIPM9502"
]
```

### Response

| HTTP Status Code | Meaning | Description          |
|------------------|---------|----------------------|
| 200              | OK      | successful operation |

## GET Request: isDccValid

Checks if DCC by pid is valid.

Request URL: https://d-si.ptb.de/api/d-dcc/dccValidation/{pid}

### Params

| Name | Location | Type   | Required | Description           |
|------|----------|--------|----------|-----------------------|
| pid  | path     | string | true     | persistent identifier |

#### Request Example:

> Request URL:
https://d-si.ptb.de/api/d-dcc/dccValidation/CCM.M-K1-NPL9507

#### Response Example

> 200 Response
> ```json
> true
> ```

> 404 Response
>```string
>Not Found 
>```

### Response

| HTTP Status Code | Meaning | Description | Data schema |
|------------------|---------|-------------|-------------|
| 200              | OK      | OK          | boolean     |
| 404              | ERROR   | Not Found   | string      |

## GET Request: getDccByPid

returns XML_Dcc encoded as StringBase64 by a specific pid.

Request URL: https://d-si.ptb.de/api/d-dcc/dcc/{pid}

### Params

| Name | Location | Type   | Required | Description           |
|------|----------|--------|----------|-----------------------|
| pid  | path     | string | true     | persistent identifier |

#### Request Example:

> Request URL:
https://d-si.ptb.de/api/d-dcc/dcc/CCM.M-K1-NPL9507

#### Response Example:

```json
"PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4KPGRjYzpkaWdpdGFsQ2FsaWJyYXRpb25DZXJ0aWZpY2F0ZSB4bWx"
```

### Response

| HTTP Status Code | Meaning | Description | Data schema |
|------------------|---------|-------------|-------------|
| 200              | OK      | OK          | string      |
| 404              | ERROR   | Not Found   | string      |

> 200 Response

> 404 Response

## GET Request: geDccContentByRefType

Return DCC_XML with searched refType attribute by a specific DccPid .

GET /api/d-dcc/dcc/{pid}/{refType}

### Params

| Name    | Location | Type   | Required | Description           |
|---------|----------|--------|----------|-----------------------|
| pid     | path     | string | yes      | persistent identifier |
| refType | path     | string | yes      | reference type        |

#### Request Example:

> Request URL:
https://d-si.ptb.de/api/d-dcc/dcc/CCM.M-K1-NPL9507/mass_mass

> Response Example

```xml

<dcc:result refType="mass_mass">
    <dcc:name>
        <dcc:content lang="en">Result: weighted mean value</dcc:content>
    </dcc:name>
    <dcc:data>
        <dcc:quantity refType="basic_nominalValue">
            <dcc:name>
                <dcc:content lang="en">nominal value</dcc:content>
            </dcc:name>
            <si:real>
                <si:value>1</si:value>
                <si:unit>\kilogram</si:unit>
            </si:real>
        </dcc:quantity>
        <dcc:quantity refType="basic_measuredValue">
            <dcc:name>
                <dcc:content lang="en">mass</dcc:content>
            </dcc:name>
            <si:real>
                <si:value>1.000478</si:value>
                <si:unit>\kilogram</si:unit>
                <si:expandedUnc>
                    <si:uncertainty>0.000032</si:uncertainty>
                    <si:coverageFactor>2</si:coverageFactor>
                    <si:coverageProbability>0.95</si:coverageProbability>
                </si:expandedUnc>
            </si:real>
        </dcc:quantity>
    </dcc:data>
</dcc:result>
```

> 200 Response

### Response

| HTTP Status Code | Meaning | Description | Data schema |
|------------------|---------|-------------|-------------|
| 200              | OK      | OK          | xml         |

# DCC Data Schema

```json
{
  "pid": "string",
  "xmlBase64": "string",
  "isDccValid": boolean
}

```

### Attribute

| Name       | Type    | Required | Restrictions | Title | Description                       |
|------------|---------|----------|--------------|-------|-----------------------------------|
| pid        | string  | true    | none         | none  | persistent identifier             |
| xmlBase64  | string  | true    | none         | none  | Xml_Dcc converted to StringBase64 |
| isDccValid | boolean | true    | none         | none  | valid or invalid DCC              |

