# Application-specific R8 rules can be added here when a dependency requires them.

# Protobuf-lite discovers each message field by its generated Java field name.
-keep class com.mattrobertson.greek.reader.** extends com.google.protobuf.GeneratedMessageLite { *; }
