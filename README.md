認証時に、追加情報が必要な場合のカストマイズ方法

### AuthenticationFilter
AuthenticationProviderに渡すTokenに認証用の情報を付加する

### AuthenticationProvider 
Tokenの情報を元に認証を実行する
ロールの設定などは、ここでカストマイズ可能

### UserDetailsService

DaoAuthenticationProviderが認証用の情報(UserDetails)を取得する参照先

### 結論

認証に関係がないが、アプリケーションで追加で使いたい情報を、認証プロセスの途中で取得するのは、良い設計ではない。

認証の関心事以外は、アプリケーション側の機能で実現する。
