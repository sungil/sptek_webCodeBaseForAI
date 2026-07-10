/*
//rest api 성공 응답 규격
HttpStatus.OK(200)
{
  "resultCode": "S000",
  "resultMessage": "success",
  "requestTime" : "2024-12-27T14:29:31.827941",
  "responseTime" : "2024-12-27T14:29:31.848168",
  "durationMsec" : "20",
  "result": { -->> T 객체에 선언된 내용으로 구성됨.
    "name": "myProject",
    "version": "v1",
    "description": "sptek web framework"
  }
}
 */

/*
//rest api 에러 응답 규격
{
    "resultCode": "GE011",
    "resultMessage": "NOT_VALID_ERROR",
    "requestTime" : "2024-12-27T14:29:31.827941",
    "responseTime" : "2024-12-27T14:29:31.848168",
    "durationMsec" : "20",
    "inValidFieldInfos": [
        {
            "field": "userName",
            "value": "s",
            "reason": "Size error"
        }
    ],
    "exceptionMessage": "Validation failed for argument [0] in protected org.springframework.http.ResponseEntity&lt;com.sptek.webfw.dto.ApiCommonSuccessResponseDto&lt;com.sptek.webfw.example.dto.ValidationTestDto&gt;&gt; com.sptek.webfw.example.api.api1.ApiTestController.validationAnnotationPost(com.sptek.webfw.example.dto.ValidationTestDto): [Field error in object 'validationTestDto' on field 'userName': rejected value [s]; codes [Size.validationTestDto.userName,Size.userName,Size.java.lang.String,Size]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [validationTestDto.userName,userName]; arguments []; default message [userName],20,2]; default message [Size error]] "
}
 */



/**
 * 공통 fetch 함수
 *
 * @param {string} url - 요청할 API URL
 * @param {Object} options - 사용자 정의 옵션 (method, headers, body, query 등)
 * @returns {Promise<any>} 응답 JSON 또는 필요한 데이터
 */
export const API_BASE_URL = document.querySelector('meta[name="apiBaseUrl"]')?.content ?? 'https://localhost/api';
//console.log("API_BASE_URL: " + API_BASE_URL);

export async function requestFetch(url, options = {}) {
    const {
        baseUrl = API_BASE_URL,       // 전역 API prefix가 있다면 사용
        method = 'GET',
        headers = {},
        body,
        query = {},
        useTimestamp = false, // true일 때 요청에 시간 스탬프 파람 추가 (동일 req 인식을 회피 하려고..)
        timeout = 600000,    // 기본 timeout
        rawResponseOpt = false, // JSON 파싱 말고 원본 응답 받고 싶을 때
        showErrorAlertOpt = true, // 에러 메시지 알림 표시 여부
        credentialsOpt = 'same-origin', //브라우저와 fetch url이 동일한 경우만
        //credentialsOpt = 'omit', //쿠키및 보안정보 항상 안보냄
        //credentialsOpt = 'include', //브라우저와 fetch url이 다르더라도 fetch url과 관련된 쿠키 및 보안정보를 보냄 (CORS 관련 Access-Control-Allow-Credentials: true 설정 필요)
    } = options;

    // 1. 쿼리 파라미터 붙이기
    const extendedQuery = {...query};
    if (useTimestamp) {
        extendedQuery.timeStamp = Date.now();
    }
    const queryString = new URLSearchParams(extendedQuery).toString();
    const fullUrl = `${baseUrl}${url}${queryString ? `?${queryString}` : ''}`;

    // 2. 요청 옵션 구성
    const fetchOptions = {
        method,
        headers: {
            ...headers,
        },
        credentials: credentialsOpt, // todo: credentials 테스트 필요
    };

    // 3. body 처리 (GET/HEAD에는 넣지 않음)
    if (body && method !== 'GET' && method !== 'HEAD') {
        // FormData인 경우: Content-Type을 브라우저가 자동으로 설정하게 둔다
        if (body instanceof FormData) {
            fetchOptions.body = body;
            // Content-Type 명시 X (브라우저가 boundary 포함한 multipart/form-data 자동 설정)
        }
        // 문자열인 경우: 그대로 사용하고 Content-Type은 직접 지정
        else if (typeof body === 'string') {
            fetchOptions.body = body;
            fetchOptions.headers['Content-Type'] = 'application/json'; // 혹은 text/plain도 가능
        }
        // 객체인 경우: JSON으로 직렬화해서 전송
        else if (typeof body === 'object') {
            fetchOptions.body = JSON.stringify(body);
            fetchOptions.headers['Content-Type'] = 'application/json';
        }
    }

    // 4. AbortController로 timeout 설정
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), timeout);
    fetchOptions.signal = controller.signal;

    // 5. 실제 처리
    try {
        const response = await fetch(fullUrl, fetchOptions);
        clearTimeout(timeoutId);

        if (rawResponseOpt) {
            return await response.text();
        }

        let responseJson = await response.json();
        if (!response.ok) {
            if (showErrorAlertOpt) {
                const code = responseJson?.resultCode ?? '';
                const message = responseJson?.resultMessage ?? '';
                const exception = responseJson?.exceptionMessage;

                if (code.startsWith('GE')) {
                    let alertMsg = '에러가 발생 하였습니다. 관리자에 문의 주세요\n\n';
                    alertMsg += `${code}\n${message}`;
                    if (exception) {
                        alertMsg += `\n${exception}`;
                    }
                    alert(alertMsg);

                } else if (code.startsWith('SE')) {
                    if (response.status === 429) {
                        console.error('Too many requests. This error has been ignored.');
                    } else {
                        if (exception) {
                            alert(exception);
                        } else {
                            alert(message || '알 수 없는 오류가 발생 했습니다.');
                        }
                    }
                }
            }
        }
        return responseJson;

    } catch (err) {
        console.log("requestFetch error: " + err)
        if (err.name === 'AbortError') {
            throw new Error(`Request timed out after ${timeout}ms`);
        }
        throw err;
    }
}

// GET 요청 (with query string)
/*
const data = await requestFetch('/api/xxx', {
    query: { page: 1, size: 20 }
});
console.log(data);
*/

// POST 요청 (with body)
/*
const user = await requestFetch('/api/users', {
    method: 'POST',
    body: { name: 'John', age: 30 }
});
console.log(user);
*/

// 커스텀 헤더 추가 (ex: Authorization)
/*
const token = 'Bearer abc123';
const data = await requestFetch('/api/protected', {
    headers: { Authorization: token }
});
*/

// 응답을 JSON 파싱하지 않고 원본 그대로 받고 싶을 때
/*const res = await requestFetch('/api/file', {
    rawResponseOpt: true
});
const blob = await res.blob(); // 파일 다운로드 등
*/

// 요청 타임아웃 설정
/*
try {
    await requestFetch('/api/slow', {
        timeout: 5000 // 5초 초과 시 에러
    });
} catch (err) {
    console.error('요청 실패:', err.message);
}
*/

export async function rsaEncrypt(plainText) {
    try {
        const response = await requestFetch('/systemSupportApi/rsaPublicKeyBase64', {
            baseUrl: API_BASE_URL.replace("/api", ""),
        });
        console.log('response:', typeof response === 'string' ? response : JSON.stringify(response, null, 2));

        const publicKeyBase64 = response.result;
        //console.log('publicKeyBase64:', publicKeyBase64);
        // publicKeyBase64 → PEM 변환
        const publicKeyPem = `-----BEGIN PUBLIC KEY-----\n${publicKeyBase64.match(/.{1,64}/g).join('\n')}\n-----END PUBLIC KEY-----`;

        const encryptor = new JSEncrypt();
        encryptor.setPublicKey(publicKeyPem);
        const rsaEncryptedText = `ENC_sptRSA(${await encryptor.encrypt(plainText)})`;
        console.log('encryptedText:', rsaEncryptedText);
        return rsaEncryptedText;

    } catch (error) {
        console.error("Error requestFetch:", error);
        throw error;
    }
}