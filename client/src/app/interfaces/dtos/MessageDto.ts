import { MessageTopic } from "../../constants/MessageTopic";

export interface MessageDto {
  topic: MessageTopic;
  data: object;
}
